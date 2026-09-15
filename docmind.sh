#!/usr/bin/env bash

set -u

SCRIPT_DIR="$(dirname -- "${BASH_SOURCE[0]}")"
cd -- "$SCRIPT_DIR" || exit 1

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
    printf '%b\n' "${GREEN}${CHECK} $1${NC}"
}

error() {
    printf '%b\n' "${RED}${CROSS} $1${NC}"
}

warning() {
    printf '%b\n' "${YELLOW}${WARNING} $1${NC}"
}

major_section() {
    local title="$1"
    printf '\n'
    printf '%b\n' "${CYAN}${title}${NC}"
}

minor_section() {
    local title="$1"
    printf '\n'
    printf '%b\n' "${BLUE}${title}${NC}"
}

check_prerequisites() {
    minor_section "Checking Environment"
    if [[ ! -f ".env" ]]; then
        error ".env file not found"
        exit 1
    fi
    success ".env found"

    minor_section "Checking Docker"
    if ! command -v docker >/dev/null 2>&1; then
        error "Docker is not installed"
        exit 1
    fi
    if ! docker info >/dev/null 2>&1; then
        error "Docker is not running"
        exit 1
    fi
    success "Docker is running"
}

validate_compose() {
    major_section "🔍 Validating Configuration"
    local compose_output
    if compose_output="$("${COMPOSE[@]}" config 2>&1)"; then
        success "Docker Compose configuration is valid"
    else
        error "Docker Compose configuration is invalid"
        printf '\n%s\n' "$compose_output"
        exit 1
    fi
}

build_missing_images() {
    major_section "🔍 Checking Docker Images"
    local images
    local image
    local need_build=0
    if ! images="$("${COMPOSE[@]}" config --images 2>/dev/null)"; then
        error "Failed to retrieve Docker images"
        exit 1
    fi
    if [[ -z "$images" ]]; then
        warning "No Docker images found"
        return
    fi
    while IFS= read -r image; do
        if [[ -z "$image" ]]; then
            continue
        fi
        if docker image inspect "$image" >/dev/null 2>&1; then
            success "$image"
        else
            warning "$image not found"
            need_build=1
        fi
    done <<< "$images"
    if [[ "$need_build" -eq 1 ]]; then
        major_section "🔨 Building Docker Images"
        local build_log
        local build_pid
        local frame=0
        local frames=('⠋' '⠙' '⠹' '⠸' '⠼' '⠴' '⠦' '⠧' '⠇' '⠏')
        build_log="$(mktemp)"
        "${COMPOSE[@]}" build >"$build_log" 2>&1 &
        build_pid=$!
        while kill -0 "$build_pid" 2>/dev/null; do
            printf '\r%b' "${CYAN}${frames[$frame]} Building Docker images...${NC}"
            frame=$(( (frame + 1) % ${#frames[@]} ))
            sleep 0.1
        done
        if wait "$build_pid"; then
            printf '\r\033[K'
            success "Docker images built successfully"
        else
            printf '\r\033[K'
            error "Docker image build failed"
            printf '\n'
            cat "$build_log"
            rm -f "$build_log"
            exit 1
        fi
        rm -f "$build_log"
    else
        success "All Docker images already exist"
        printf '\n'
        printf '%s\n' "Skipping build."
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
    printf '%s\n' "Waiting for services to become healthy..."
    printf '\n'
    local services=(
        "postgres"
        "qdrant"
        "redis"
        "docmind"
        "frontend"
    )
    declare -A service_status
    local start_time
    local current_time
    local elapsed
    local service
    local container
    local status
    local health
    start_time="$(date +%s)"
    while true; do
        local all_done=1
        local failed_found=0
        current_time="$(date +%s)"
        elapsed=$((current_time - start_time))
        for service in "${services[@]}"; do
            container="$("${COMPOSE[@]}" ps -q "$service" 2>/dev/null)"
            if [[ -z "$container" ]]; then
                service_status["$service"]="not_found"
                all_done=0
                continue
            fi
            status="$(
                docker inspect \
                    --format='{{.State.Status}}' \
                    "$container" 2>/dev/null
            )"
            health="$(
                docker inspect \
                    --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' \
                    "$container" 2>/dev/null
            )"
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
        status="${service_status[$service]:-unknown}"
        printf '%-20s' "$service"
        case "$status" in
            healthy)
                printf '%b\n' "${GREEN}${CHECK} Healthy${NC}"
                ;;
            running)
                printf '%b\n' "${GREEN}${CHECK} Running${NC}"
                ;;
            starting)
                printf '%b\n' "${YELLOW}${WAIT} Starting${NC}"
                failed=1
                ;;
            unhealthy)
                printf '%b\n' "${RED}${CROSS} Unhealthy${NC}"
                failed=1
                ;;
            failed)
                printf '%b\n' "${RED}${CROSS} Failed${NC}"
                failed=1
                ;;
            not_found)
                printf '%b\n' "${RED}${CROSS} Not Found${NC}"
                failed=1
                ;;
            *)
                printf '%b\n' "${RED}${CROSS} Unknown${NC}"
                failed=1
                ;;
        esac
    done
    if [[ "$failed" -ne 0 ]]; then
        printf '\n'
        error "Some services are not healthy"
        printf '\n'
        printf '%s\n' "Check logs with:"
        printf '%b\n' "${CYAN}./docmind.sh logs${NC}"
        printf '\n'
        exit 1
    fi
}

stop_containers() {
    major_section "🛑 Stopping DocMind"
    if "${COMPOSE[@]}" down; then
        printf '\n'
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
    local service="${1:-}"
    local lines="${2:-}"
    local services=(
        "postgres"
        "qdrant"
        "redis"
        "docmind"
        "frontend"
    )
    if [[ -n "$service" ]]; then
        case "$service" in
            postgres|qdrant|redis|docmind|frontend)
                ;;
            *)
                error "Unknown service: $service"
                printf '\n'
                printf '%s\n' "Available services:"
                printf '%s\n' "  postgres"
                printf '%s\n' "  qdrant"
                printf '%s\n' "  redis"
                printf '%s\n' "  docmind"
                printf '%s\n' "  frontend"
                exit 1
                ;;
        esac
    fi
    if [[ -n "$lines" ]]; then
        if ! [[ "$lines" =~ ^[0-9]+$ ]] || [[ "$lines" -eq 0 ]]; then
            error "Log limit must be a positive number"
            exit 1
        fi
    fi
    if [[ -n "$service" && -n "$lines" ]]; then
        major_section "📜 ${service} Logs"
        "${COMPOSE[@]}" logs --tail="$lines" -f "$service"
    elif [[ -n "$service" ]]; then
        major_section "📜 ${service} Logs"
        "${COMPOSE[@]}" logs -f "$service"
    elif [[ -n "$lines" ]]; then
        major_section "📜 DocMind Logs"
        "${COMPOSE[@]}" logs --tail="$lines" -f
    else
        major_section "📜 DocMind Logs"
        "${COMPOSE[@]}" logs -f
    fi
}

start() {
    major_section "🚀 Starting DocMind"
    check_prerequisites
    validate_compose
    build_missing_images
    start_containers
    wait_for_services

    major_section "🌐 DocMind Services"
    printf '%-10s : %s\n' "Frontend" "http://localhost:5173"
    printf '%-10s : %s\n' "Backend" "http://localhost:8080"
    printf '%-10s : %s\n' "Swagger" "http://localhost:8080/swagger-ui.html"
    printf '%-10s : %s\n' "Qdrant" "http://localhost:6333/dashboard"
    printf '\n'
    printf '%b\n' "${GREEN}${CHECK} DocMind is successfully up and running.${NC}"
}

usage() {
    printf '\n'
    printf '%s\n' "Usage:"
    printf '  %s\n' "$0 start                  Build missing images and start services"
    printf '  %s\n' "$0 stop                   Stop and remove containers"
    printf '  %s\n' "$0 restart                Restart services"
    printf '  %s\n' "$0 status                 Show service status"
    printf '  %s\n' "$0 logs                   Follow all service logs"
    printf '  %s\n' "$0 logs <service>         Follow service logs"
    printf '  %s\n' "$0 logs <service> <lines> Follow service logs with line limit"
    printf '  %s\n' "$0 help                   Show this help message"
    printf '\n'
    printf '%s\n' "Services:"
    printf '  %s\n' "postgres"
    printf '  %s\n' "qdrant"
    printf '  %s\n' "redis"
    printf '  %s\n' "docmind"
    printf '  %s\n' "frontend"
    printf '\n'
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
        show_logs "${2:-}" "${3:-}"
        ;;
    help)
        usage
        ;;
    *)
        error "Unknown command: $COMMAND"
        usage
        exit 1
        ;;
esac