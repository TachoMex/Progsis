	ORG	$E000	
	ldaa #$89
	adda #$78
	daa		
	stab $0902
	clra
	ldaa 32
	adda	33
OTRO	staa	@10
	ldx	$900
	jmp	OTRO
	END
