	ORG  $17
UNO    EQU  $30
DOS    EQU  $50
e1	BRA  UNO
	LBRA DOS
	BCS  UNO
	LBCS TRES
TRES   BCS  DOS
CUATRO SWI
CINCO	DS	10
	BRA	CINCO
SEIS	DS	4096
;	bcc	$17
	lbcc	$17
;	bcc	UNO
	lbcc	UNO
	lblo	SEIS
	lblo	e1
;	bcc	error
;error	bcc	$01
;e3 beq 32
;e5 bgt @75
; bhi @57
; bhi e3
	END
