/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class PAPenalidadeProcessoBO {
    
    private IHabilitacaoService habilitacaoService;
    
    public IHabilitacaoService getHabilitacaoService() {

        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }

        return habilitacaoService;
    }
    
    /**
     * Gravar penalidade.
     * 
     * @param em
     * @param pa
     * @param idUsuario
     * @param dataInicio
     * @return
     * @throws AppException 
     */
    public PAPenalidadeProcesso gravarPenalidadeControleCnh(EntityManager em, 
                                            ProcessoAdministrativo pa, 
                                            Long idUsuario, 
                                            Date dataInicio) throws AppException {
        
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcesso();
        
        PAPenalidadeProcesso penalidadeAnterior = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, pa.getId());
        
        if (penalidadeAnterior != null) {
            
            penalidadeAnterior.setAtivo(AtivoEnum.DESATIVADO);
            
            new PAPenalidadeProcessoRepositorio().update(em, penalidadeAnterior);
            
            em.flush();
        }
        
        PAComplemento complemento = 
                (PAComplemento) new PAComplementoRepositorio().
                        getPAComplementoPorNumeroPAEParametroEAtivo(em, 
                                                                    pa.getNumeroProcesso(), 
                                                                    PAParametroEnum.TEMPO_PENALIDADE);
        
        BigDecimal tempo = new BigDecimal(complemento.getValor());
        
        penalidade.setDataInicioPenalidade(dataInicio);
        
        penalidade.setProcessoAdministrativo(pa);
        penalidade.setUsuario(idUsuario);
        penalidade.setDataCadastro(new Date());
        penalidade.setAtivo(AtivoEnum.ATIVO);
        penalidade.setValor(tempo.intValue());
        
        if (pa.isJuridico()) {
            penalidade.setUnidadePenal(UnidadePenalEnum.DIA);
            penalidade.setDataFimPenalidade(Utils.addDayMonth(Utils.addDayMonth(dataInicio, -1), tempo.intValue()));
        }else if(!pa.getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)){
            penalidade.setUnidadePenal(UnidadePenalEnum.MES);
            penalidade.setDataFimPenalidade(Utils.addMonth(Utils.addDayMonth(dataInicio, -1), tempo.intValue()));
        } else {
            penalidade.setUnidadePenal(UnidadePenalEnum.MES);
            penalidade.setDataFimPenalidade(Utils.addDayMonth(dataInicio, 1));
        }

        return new  PAPenalidadeProcessoRepositorio().insert(em, penalidade);
        
    }

}