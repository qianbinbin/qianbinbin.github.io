	.file	"fact_for.c"
	.text
	.globl	fact_for
	.type	fact_for, @function
fact_for:
.LFB0:
	.cfi_startproc
	movl	$1, %eax
	movl	$2, %edx
.L2:
	cmpq	%rdi, %rdx
	jg	.L4
	imulq	%rdx, %rax
	addq	$1, %rdx
	jmp	.L2
.L4:
	ret
	.cfi_endproc
.LFE0:
	.size	fact_for, .-fact_for
	.ident	"GCC: (Debian 8.3.0-6) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
