#!/usr/bin/env bash
set -u
cd "$(dirname "$0")" || exit 1

COMPOSE=(docker compose --env-file .env)

MAX_WAIT=120
INTERVAL=3

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
NC='\033[0m'

CHECK="✓"
CROSS="✗"
WARNING="⚠"
WAIT="⏳"

success() {
    echo -e "${GREEN}${CHECK} $1${NC}"
}

error() {
    echo -e "${RED}${CROSS} $1${NC}"
}

warning() {
    echo -e "${YELLOW}${WARNING} $1${NC}"
}

major_section() {
    local title="$1"
    echo ""
    echo -e "${CYAN}${title}${NC}"
}

minor_section() {
    local title="$1"
    echo ""
    echo -e "${BLUE}${title}${NC}"
}

check_prerequisites() {
    minor_section "Checking Environment"
    if [[ ! -f ".env" ]]; then
        error ".env file not found"
        exit 1
    fi
    success ".env found"

    minor_section "Checking Docker"
    if ! docker info > /dev/null 2>&1; then
        error "Docker is not running"
        exit 1
    fi
    success "Docker is running"
}

validate_compose() {
    major_section "🔍 Validating Configuration"
    if "${COMPOSE[@]}" config > /dev/null 2>&1; then
        success "Docker Compose configuration is valid"
    else
        error "Docker Compose configuration is invalid"
        echo ""
        "${COMPOSE[@]}" config
        exit 1
    fi
}

build_missing_images() {
    major_section "🔍 Checking Docker Images"
    local images
    local need_build=0
    images=$("${COMPOSE[@]}" config --images 2>/dev/null)
    if [[ -z "$images" ]]; then
        warning "No Docker images found"
        return
    fi
    while IFS= read -r image; do
        [[ -z "$image" ]] && continue
        if docker image inspect "$image" > /dev/null 2>&1; then
            success "$image"
        else
            warning "$image not found"
            need_build=1
        fi
    done <<< "$images"
    if [[ "$need_build" -eq 1 ]]; then
        major_section "🔨 Building Docker Images"
        if "${COMPOSE[@]}" build; then
            success "Docker images built successfully"
        else
            error "Docker image build failed"
            exit 1
        fi
    else
        success "All Docker images already exist"
        echo ""
        echo "Skipping build."
    fi
}

start_containers() {
    major_section "🐳 Starting Containers"
    if "${COMPOSE[@]}" up -d; then
        success "Containers started in detached mode"
    else
        error "Failed to start containers"
        exit 1
    fi
}

wait_for_services() {
    major_section "⏳ Checking Services"
    echo "Waiting for services to become healthy..."
    echo ""
    local services=(
        "postgres"
        "qdrant"
        "redis"
        "docmind"
        "frontend"
    )
    declare -A service_status
    local start_time
    start_time=$(date +%s)
    while true; do
        local all_done=1
        local failed_found=0
        local current_time
        local elapsed
        current_time=$(date +%s)
        elapsed=$((current_time - start_time))
        for service in "${services[@]}"; do
            local container
            local status
            local health
            container=$("${COMPOSE[@]}" ps -q "$service" 2>/dev/null)
            if [[ -z "$container" ]]; then
                service_status["$service"]="not_found"
                all_done=0
                continue
            fi
            status=$(docker inspect \
                --format='{{.State.Status}}' \
                "$container" 2>/dev/null)
            health=$(docker inspect \
                --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' \
                "$container" 2>/dev/null)
            if [[ "$status" != "running" ]]; then
                service_status["$service"]="failed"
                failed_found=1
            elif [[ "$health" == "healthy" ]]; then
                service_status["$service"]="healthy"
            elif [[ "$health" == "unhealthy" ]]; then
                service_status["$service"]="unhealthy"
                failed_found=1
            elif [[ "$health" == "starting" ]]; then
                service_status["$service"]="starting"
                all_done=0
            elif [[ "$health" == "none" ]]; then
                service_status["$service"]="running"
            else
                service_status["$service"]="starting"
                all_done=0
            fi
        done
        if [[ "$elapsed" -ge "$MAX_WAIT" ]]; then
            warning "Service health check timed out after ${MAX_WAIT}s"
            break
        fi
        if [[ "$failed_found" -eq 1 || "$all_done" -eq 1 ]]; then
            break
        fi
        sleep "$INTERVAL"
    done

    major_section "📊 Service Status"
    local failed=0
    for service in "${services[@]}"; do
        local status="${service_status[$service]:-unknown}"
        printf "%-20s" "$service"
        case "$status" in
            healthy)
                echo -e "${GREEN}${CHECK} Healthy${NC}"
                ;;
            running)
                echo -e "${GREEN}${CHECK} Running${NC}"
                ;;
            starting)
                echo -e "${YELLOW}${WAIT} Starting${NC}"
                failed=1
                ;;
            unhealthy)
                echo -e "${RED}${CROSS} Unhealthy${NC}"
                failed=1
                ;;
            failed)
                echo -e "${RED}${CROSS} Failed${NC}"
                failed=1
                ;;
            not_found)
                echo -e "${RED}${CROSS} Not Found${NC}"
                failed=1
                ;;
            *)
                echo -e "${RED}${CROSS} Unknown${NC}"
                failed=1
                ;;
        esac
    done
    if [[ "$failed" -ne 0 ]]; then
        echo ""
        error "Some services are not healthy"
        echo ""
        echo "Check logs with:"
        echo -e "${CYAN}${COMPOSE[*]} logs${NC}"
        echo ""
        exit 1
    fi
}

stop_containers() {
    major_section "🛑 Stopping DocMind"
    if "${COMPOSE[@]}" down; then
        echo ""
        success "All containers stopped and removed"
    else
        error "Failed to stop containers"
        exit 1
    fi
}

restart_containers() {
    major_section "🔄 Restarting DocMind"
    if "${COMPOSE[@]}" down; then
        success "Existing containers stopped"
    else
        error "Failed to stop existing containers"
        exit 1
    fi
    if "${COMPOSE[@]}" up -d; then
        success "Containers restarted"
    else
        error "Failed to restart containers"
        exit 1
    fi
    wait_for_services
}

show_status() {
    major_section "📊 DocMind Status"
    "${COMPOSE[@]}" ps
}

show_logs() {
    major_section "📜 DocMind Logs"
    "${COMPOSE[@]}" logs -f
}

start() {
    major_section "🚀 Starting DocMind"
    check_prerequisites
    validate_compose
    build_missing_images
    start_containers
    wait_for_services

    major_section "🌐 DocMind Services"
    printf "%-10s : %s\n" "Frontend" "http://localhost:5173"
    printf "%-10s : %s\n" "Backend"  "http://localhost:8080"
    printf "%-10s : %s\n" "Swagger"  "http://localhost:8080/swagger-ui.html"
    printf "%-10s : %s\n" "Qdrant"   "http://localhost:6333/dashboard"
    echo ""
    echo -e "${GREEN}${CHECK} DocMind is successfully up and running.${NC}"
}

usage() {

    echo ""
    echo "Usage:"
    echo "  $0 start      Build missing images and start services"
    echo "  $0 stop       Stop and remove containers"
    echo "  $0 restart    Restart services"
    echo "  $0 status     Show service status"
    echo "  $0 logs       Follow service logs"
    echo "  $0 help       Show this help message"
    echo ""

}

COMMAND="${1:-start}"

case "$COMMAND" in

    start)
        start
        ;;

    stop)
        check_prerequisites
        stop_containers
        ;;

    restart)
        check_prerequisites
        restart_containers
        ;;

    status)
        check_prerequisites
        show_status
        ;;

    logs)
        check_prerequisites
        show_logs
        ;;

    -h|--help|help)
        usage
        ;;

    *)
        error "Unknown command: $COMMAND"
        usage
        exit 1
        ;;

esac
