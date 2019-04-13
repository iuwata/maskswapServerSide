package com.elefthes.maskswap.util;

public enum StatusCode {
    Failure(0),
    Success(1),
    NeedLogin(2),
    EmailAlreadyExist(3),
    IncompleteEmail(4),
    IncompletePassword(5),
    EmailDoesNotExist(6),
    IncorrectPassword(7),
    EmailAlreadyAuthenticated(8),
    EmailAuthenticationExpired(9),
    IncorrectAuthenticationCode(10),
    NoSrcVideo(11),
    NoDstVideo(12),
    VirusFound(13),
    NoOrder(14),
    IncompleteOrder(15),
    CompleteOrder(16),
    VideosNotUploaded(17),
    SrcVideoUploaded(18),
    DstVideoUploaded(19),
    VideosUploaded(20),
    OrderAlreadyExist(21),
    NoPlan(22),
    VideoAlreadyExist(23),
    PaymentFailure(24),
    PaymentDataBaseFailure(25),
    CheckPaymentFailure(26),
    AlreadyPaid(27),
    NoSrcImage(28),
    NoDstImage(29),
    ImageAlreadyExist(30),
    NoDuration(31);
    
    
    
    
    private int id;
    
    private StatusCode(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
