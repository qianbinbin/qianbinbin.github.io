	.file	"cmovdiff.c"
	.text
	.globl	cmovdiff
	.type	cmovdiff, @function
cmovdiff:
.LFB0:
	.cfi_startproc
	movq	%rsi, %rdx
	subq	%rdi, %rdx
	movq	%rdi, %rax
	subq	%rsi, %rax
	cmpq	%rdi, %rsi
	cmovg	%rdx, %rax
	ret
	.cfi_endproc
.LFE0:
	.size	cmovdiff, .-cmovdiff
	.ident	"GCC: (Debian 8.3.0-6) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
