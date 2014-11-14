    ORG $6100
INI LDAA $0800
    bsr asc2BCD
    staa $0803
    ldaa $0801
    bsr asc2bcd
    staa $0804
    ldaa $0802
    bsr asc2bcd     
    staa $0805
    bra ini
asc2bcd cmpa #$30
    bcs ERR
    cmpa #$39
    bls CON
ERR ldaa #$2F
CON suba #$30
    rts
    END  