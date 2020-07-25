	.file	"fact_while.c"
	.text
	.globl	fact_while
	.type	fact_while, @function
fact_while:
.LFB0:
	.cfi_startproc
	movl	$1, %eax
.L2:
	cmpq	$1, %rdi
	jle	.L4
	imulq	%rdi, %rax
	subq	$1, %rdi
	jmp	.L2
.L4:
	ret
	.cfi_endproc
.LFE0:
	.size	fact_while, .-fact_while
	.ident	"GCC: (Debian 8.3.0-6) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
