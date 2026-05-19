package com.hardeymorlah.walletapi.exception;

public class ReceiverWalletNotFoundException extends RuntimeException {

    public ReceiverWalletNotFoundException(String message) {
        super(message);
    }
}