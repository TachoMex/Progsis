	org 	$0900
ini	ldx		#$0800
	ldaa	#$B6
	staa	2,PC
	stx		1,PC
	DS.B 	3
	adda	$0801
	staa	$0802
	bra 	INI
	END