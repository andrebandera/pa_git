package br.gov.ms.detran.processo.administrativo.util;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Lillydi
 */
public class ProcessoAdministrativoUtil{

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoUtil.class);

    public Integer getAnoAtual() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static void agruparArquivosPdf(String pathArquivo, List<byte[]> pdfs) throws AppException {

        try {

            PdfDocument pdfFinal = new PdfDocument(new PdfWriter(pathArquivo));
            PdfMerger merger = new PdfMerger(pdfFinal);

            for (byte[] arquivo : pdfs) {

                try{

                    ByteArrayInputStream pdf = new ByteArrayInputStream(arquivo);
                    PdfReader readerPdf = new PdfReader(pdf);
                    PdfDocument pdfDoc = new PdfDocument(readerPdf.setUnethicalReading(true));
                    merger.merge(pdfDoc,1,pdfDoc.getNumberOfPages());

                    readerPdf.close();

                }catch(Exception e){
                    LOG.error("Erro no agrupamento de pdf", e);
                }
            }

            merger.close();
            pdfFinal.close();

          } catch (IOException ex) {
            LOG.error("Capturado.", ex);
            throw new AppException(ex);
        }
    }
}
