	ORG	1125			
	STS	[@125,PC]	
A1	STS	[D,SP]		

	JMP  [D,PC]		
	ADCA [D,X]		
	ADCB [D,SP]		
A4	ADDA [D,Y]		

	LDAA [10,X]		
	LDAA [31483,X]		

	STS	[$0FFFF,PC]	
	LDAA	[%010,X]	
a3	LDAA	[@3143,X]	
	STS	[125,PC]	
	END
