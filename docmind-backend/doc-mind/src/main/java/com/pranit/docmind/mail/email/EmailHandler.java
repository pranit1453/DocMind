package com.pranit.docmind.mail.email;

import com.pranit.docmind.entities.constant.EmailPurpose;

public interface EmailHandler<T> {

    EmailPurpose supports();

    void send(T event);
}