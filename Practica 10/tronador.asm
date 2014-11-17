	Org $1000
	Adca [0,x] 
	Adca [0,y] 
	Adca [0,sp] 
	Adca [0,pc] 
	swi
	Ds.b $20
	Adca [0,x] 
	adca [0,y] 
	adca [0,pc]
	fcc "abcde"
	Swi 
	Ds.b $1
	Swi 
	Ds.b $1
	Swi
	Swi
	Swi
	Swi
	Swi
	Ds.b $1
	Fcc "abcde" 
	end 
