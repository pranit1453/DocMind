package com.pranit.docmind.mail.email;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.mail.dto.OtpEvent;

public interface OtpEmailHandler {

    OtpPurpose supports();

    void send(OtpEvent event);
}
