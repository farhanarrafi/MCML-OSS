
CFLAGS= -DHAVE_CONFIG_H -I../include -fPIC -O2 -m32

OBJS= bits.o \
	buffer.o \
	cb_search.o \
	exc_10_16_table.o \
	exc_10_32_table.o \
	exc_20_32_table.o \
	exc_5_256_table.o \
	exc_5_64_table.o \
	exc_8_128_table.o \
	filters.o \
	gain_table.o \
	gain_table_lbr.o \
	hexc_10_32_table.o \
	hexc_table.o \
	high_lsp_tables.o \
	lpc.o \
	lsp.o \
	lsp_tables_nb.o \
	ltp.o \
	modes.o \
	modes_wb.o \
	nb_celp.o \
	quant_lsp.o \
	resample.o \
	sb_celp.o \
	speex.o \
	speex_callbacks.o \
	speex_header.o \
	stereo.o \
	vbr.o \
	vq.o \
	window.o

all: $(OBJS)

%.o: %.c
	$(CC) $(CFLAGS) -o $@ -c $<

