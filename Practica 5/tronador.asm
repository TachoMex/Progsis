	ORG	$E000
	ldaa	#$59	;A<=59H
	adda	#$78	;A<=78H
	daa
	stab	$0802
	clra
	ldaa	16
	adda	17

ini	staa	@10
	ldx	$800
	jmp ini
	jmp	fin
fin	END
