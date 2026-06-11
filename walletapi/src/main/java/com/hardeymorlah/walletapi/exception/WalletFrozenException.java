package com.hardeymorlah.walletapi.exception;

public class WalletFrozenException extends RuntimeException {

    public WalletFrozenException(String message) {
        super(message);
    }
}
