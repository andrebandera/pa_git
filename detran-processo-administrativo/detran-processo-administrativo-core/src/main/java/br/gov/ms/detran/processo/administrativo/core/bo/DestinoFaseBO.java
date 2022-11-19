package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoRecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import javax.persistence.EntityManager;

public class DestinoFaseBO {
    
    private static final Logger LOG = Logger.getLogger(DestinoFaseBO.class);

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @param tipoRecurso
     * @return
     * @throws AppException 
     */
    public OrigemDestinoEnum getDefineOrigemDestinoParaProcessoAdministrativo(
        EntityManager em, ProcessoAdministrativo processoAdministrativo, TipoFasePaEnum tipoRecurso) throws AppException {
        
        OrigemDestinoEnum resultadoOrigemDestino = null;
        
        if(!TipoFasePaEnum.PENALIZACAO.equals(tipoRecurso)) {
        
            resultadoOrigemDestino = OrigemDestinoEnum.SEPEN;
        
        } else {
        
            if(TipoFasePaEnum.PENALIZACAO.equals(tipoRecurso)) {

                Recurso recursoAnterior 
                    = new RecursoRepositorio()
                        .getRecursoMaisRecentePorProcessoEFase(
                            em, 
                            processoAdministrativo.getId(), 
                            tipoRecurso
                        );

                if(recursoAnterior == null) {

                    resultadoOrigemDestino = OrigemDestinoEnum.JARI;

                } else {

                    if(OrigemDestinoEnum.JARI.equals(recursoAnterior.getDestinoFase().getOrigemDestino())) {

                        ResultadoRecurso resultado 
                            = new ResultadoRecursoRepositorio()
                                .getResultadoRecursoAtivoPorRecurso(em, recursoAnterior.getId());
                        
                        if(resultado != null 
                                && (ResultadoRecursoEnum.IMPROVIDO.equals(resultado.getResultado())
                                        || ResultadoRecursoEnum.NAO_CONHECIMENTO.equals(resultado.getResultado()))){

                            resultadoOrigemDestino = OrigemDestinoEnum.CETRAN;
                        }
                    }
                }
            }
        }
        
        return resultadoOrigemDestino;
    }
}