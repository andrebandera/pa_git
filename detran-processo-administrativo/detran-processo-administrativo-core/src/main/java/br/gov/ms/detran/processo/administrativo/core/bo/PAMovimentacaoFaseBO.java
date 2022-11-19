/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAMovimentacaoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAMovimentacaoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAMovimentacaoFaseBO {
    
    public void validarPAParaExecucaoServico(EntityManager em, String numeroProcesso, String servico) throws AppException{
        
        List<PAMovimentacaoFase> listMovimentacaoFase = new PAMovimentacaoFaseRepositorio().getMovimentacaoFasePorServico(em, servico);
        if(DetranCollectionUtil.ehNuloOuVazio(listMovimentacaoFase)){
            DetranWebUtils.applicationMessageException("Não existem andamentos relacionados a este serviço.");
        }

        PAOcorrenciaStatus ocorrencia = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtivaPorNumeroProcesso(em, numeroProcesso);
        if(ocorrencia == null){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o andamento do PA informado.");
        }
        //Pega o fluxo fase da lista
        List<Long> lista = Lists.transform(listMovimentacaoFase, new Function<PAMovimentacaoFase, Long>() {
            @Override
            public Long apply(PAMovimentacaoFase f) {
                if(f!= null && f.getFluxoFase() != null && f.getFluxoFase().getPrioridadeFluxoAmparo() != null && f.getFluxoFase().getPrioridadeFluxoAmparo().getFluxoProcesso() != null){
                    return f.getFluxoFase().getId();
                }
                return null;
            }
        });
        
        PAFluxoFase fluxoFase = new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, ocorrencia.getProcessoAdministrativo());
        
        //Verifica se o fluxo fase da ocorrência contém na lista através da comparação de IDs
        if(!lista.contains(fluxoFase.getId())){
            DetranWebUtils.applicationMessageException("O andamento do PA não permite a execução deste serviço.");
        }
    }
}
