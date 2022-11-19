package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.util.ContentTypeConverterFactory;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JsonJacksonUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.MediaType;



public class ExecucaoAndamentoWrapper {

    private String numeroProcesso;
    
    private PAAndamentoEnum andamento;

    private Object objetoEntrada;

    public ExecucaoAndamentoWrapper() {
    }

    public ExecucaoAndamentoWrapper(PAAndamentoEnum andamento) {
        this.andamento = andamento;
    }
    
    public ExecucaoAndamentoWrapper(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public ExecucaoAndamentoWrapper(String numeroProcesso, PAAndamentoEnum andamento, Object... objetoEntrada) {
        this.numeroProcesso = numeroProcesso;
        this.andamento      = andamento;
        this.objetoEntrada  = objetoEntrada;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Object getObjetoEntrada() {
        return objetoEntrada;
    }

    public void setObjetoEntrada(Object objetoEntrada) throws AppException, IOException {
        
        this.objetoEntrada = objetoEntrada;
        
        if(this.andamento != null && this.andamento.getObjetoEntrada() != null && objetoEntrada != null) {
        
            ContentTypeConverterFactory converter 
                = ContentTypeConverterFactory.getContentTypeConverter(MediaType.APPLICATION_JSON);

            List objArrayEntrada = (List)objetoEntrada;
            if (!DetranCollectionUtil.ehNuloOuVazio(objArrayEntrada)) {
                String objetoEntradaStr = converter.fromObject(objArrayEntrada.get(0));

                Object[] objArray = (Object[]) this.andamento.getObjetoEntrada();
                if (objArray[0] != null) {
                    this.objetoEntrada
                            = JsonJacksonUtils
                                    .forObject(
                                            objetoEntradaStr,
                                            objArray[0].getClass()
                                    );
                }
            }
        }
    }

    public PAAndamentoEnum getAndamento() {
        return andamento;
    }

    public void setAndamento(PAAndamentoEnum andamento) {
        this.andamento = andamento;
    }
}