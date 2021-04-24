	.file	"arith.c"
	.text
	.globl	arith
	.type	arith, @function
arith:
.LFB0:
	.cfi_startproc
	leaq	(%rdx,%rdx,2), %rax
	salq	$4, %rax
	xorq	%rsi, %rdi
	andl	$252645135, %edi
	subq	%rdi, %rax
	ret
	.cfi_endproc
.LFE0:
	.size	arith, .-arith
	.ident	"GCC: (Debian 8.3.0-6) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
