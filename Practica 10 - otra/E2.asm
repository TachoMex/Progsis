	ORG	$8000
	ldx	#$0800
	clra
LIMPIA	clr A,X
	inca
	cmpa	#$00
	bne		LIMPIA
	bra		$8000
	END
