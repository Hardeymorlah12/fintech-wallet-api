package com.hardeymorlah.walletapi.exception;


    public class EmailAlreadyExistsException extends RuntimeException {

        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
