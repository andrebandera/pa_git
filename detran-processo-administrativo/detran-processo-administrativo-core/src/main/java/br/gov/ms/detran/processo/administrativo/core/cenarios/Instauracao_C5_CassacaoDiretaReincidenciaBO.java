package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */

/**
 * Cenario C5
 * @author desenvolvimento
 */
public class Instauracao_C5_CassacaoDiretaReincidenciaBO extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C5_CassacaoDiretaReincidenciaBO.class);

    public Instauracao_C5_CassacaoDiretaReincidenciaBO() {
        super();
    }

    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        
        DadosCondutorPAD dadosCondutorPAD = (DadosCondutorPAD) wrapper.getDadosCondutorPAD();
        
        regraGeralInfracaoBO.validarCondutorSemProcessoAdministrativo(em, dadosCondutorPAD);

        Map<String, Collection<DadosInfracaoPAD>> mapInfracoes = regraGeralInfracaoBO.gerarMapInfracoes(wrapper);

        for (Map.Entry<String, Collection<DadosInfracaoPAD>> entry : mapInfracoes.entrySet()) {

            List<DadosInfracaoPAD> infracoesValidadas = regraGeralInfracaoBO.filtrarInfracoesValidasRegrasGerais(em, entry.getValue(), wrapper);

            if (infracoesValidadas.size() > 1) {

                for (DadosInfracaoPAD infracao : infracoesValidadas) {
                    try {
                        List<DadosInfracaoPAD> infracoes = regraGeralInfracaoBO.montaInfracoesParaPA(infracao, wrapper, infracoesValidadas);
                        
                        return gravar(em, wrapper, infracoes);
                    } catch (RegraNegocioException e) {
                        //LOG.warn("Validação infração: {0}", e.getMessage());
                    }
                }
            }
        }

        return null;
    }
    
    @Override
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws DatabaseException {
        
        return 
            new DadosInfracaoPADRepositorio()
                .getInfracoesPara_C5(
                    em, 
                    wrapper.getDadosCondutorPAD().getCpf(), 
                    wrapper.getApoioOrigemInstauracao()
                );
    }
}