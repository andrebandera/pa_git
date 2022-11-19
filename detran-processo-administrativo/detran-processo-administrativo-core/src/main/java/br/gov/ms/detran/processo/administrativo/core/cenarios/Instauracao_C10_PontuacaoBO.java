
package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.entidade.enums.inf.ClassificacaoPenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ValorReferenciaLegalRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ValorReferenciaLegal;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;

public class Instauracao_C10_PontuacaoBO extends InstauracaoCenario implements IInstauracaoCenario {
    
    private static final Logger LOG = Logger.getLogger(Instauracao_C10_PontuacaoBO.class);
    
    public Instauracao_C10_PontuacaoBO() {
        super();
    }

    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        
        validarProcessoPorPontuacaoExistente(wrapper, em);

        List<DadosInfracaoPAD> conjuntoInfracoesPA = montarConjuntoInfracoesParaPontuacao(em, wrapper);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(conjuntoInfracoesPA)) {
            return gravar(em, wrapper, conjuntoInfracoesPA);
        }
        
        return null;
    }

    private void validarProcessoPorPontuacaoExistente(InstaurarProcessoAdministrativoWrapper wrapper, EntityManager em) throws RegraNegocioException, DatabaseException {
        List<ProcessoAdministrativo> lista= new ProcessoAdministrativoRepositorio().
                getProcessoAdministrativoSuspensaoAtivo(wrapper.getDadosCondutorPAD().getCpf(), em);
        if(!DetranCollectionUtil.ehNuloOuVazio(lista)){
            throw  new RegraNegocioException("Já existe PA de pontuação para este condutor.");
        }
    }
        
    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public List<DadosInfracaoPAD> montarConjuntoInfracoesParaPontuacao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        Integer pontuacao = 0;
        List<DadosInfracaoPAD> conjuntoInfracoesPA = new ArrayList<>();

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {

            Iterator<DadosInfracaoPAD> iterator = wrapper.getInfracoes().iterator();
            do {

                try {

                    DadosInfracaoPAD item = iterator.next();

                    regraGeralInfracaoBO.validarRegraInfracaoPrescrita(item, wrapper.getValorReferenciaMes());
                    regraGeralInfracaoBO.validarRegraInfracaoCometidaAposPeriodoPermissionado(item);
                    regraGeralInfracaoBO.validarCodigoAcaoInstauracao(em, item, wrapper);
                    
                    validarTipoProcessoSuspensao(em, item, wrapper);
                    validarMotivoPenalidadeQuantidadePontos(item, wrapper);

                    conjuntoInfracoesPA.add(item);
                    pontuacao += item.getQdePontosInfracao();

                } catch (RegraNegocioException ex) {
                    LOG.warn("Montar Conjunto Infracoes Pontuação: {0}", ex.getMessage());
                }

            } while (iterator.hasNext());
        }

        wrapper.setPontuacao(pontuacao);
        return pontuacao > PONTUACAO_MAXIMA ? conjuntoInfracoesPA : null;
    }

    @Override
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws DatabaseException {
        
        return new DadosInfracaoPADRepositorio().getInfracoesParaCenarioPontuacao(em, 
                                                                                  wrapper.getDadosCondutorPAD().getCpf(), 
                                                                                  wrapper.getApoioOrigemInstauracao(),
                                                                                  ClassificacaoPenalEnum.PONTUACAO);
    }

    @Override
    protected void gravarComplementoPA(EntityManager em,  ProcessoAdministrativo processoAdministrativo, List<DadosInfracaoPAD> infracoes, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        PAComplemento complemento = new PAComplemento();
            complemento.setAtivo(AtivoEnum.ATIVO);
            complemento.setParametro(PAParametroEnum.TEMPO_PENALIDADE);
            complemento.setProcessoAdministrativo(processoAdministrativo);
            complemento.setValor(getValorPenal(em, wrapper.getApoioOrigemInstauracao(), wrapper.getPontuacao()));
            
            new AbstractJpaDAORepository().insert(em, complemento);
    }
    /**
     * @param item
     * @param wrapper
     * @throws AppException 
     */
    private void validarTipoProcessoSuspensao(EntityManager em, DadosInfracaoPAD item, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        if (!new PAInfracaoRepositorio().isTipoProcessoDeSuspensao(
                em, item.getInfracaoCodigo(),
                wrapper.getApoioOrigemInstauracao().getAmparoLegal(), 
                wrapper.getApoioOrigemInstauracao().getResultadoAcao().ordinal(), 
                wrapper.getApoioOrigemInstauracao().getAcaoSistema().ordinal())) {

            throw new RegraNegocioException("Tipo processo não é Suspensão."
                    + "" + item.getIsn()
                    + "" + item.getCpfCondutor().getCpf()
                    + "" + item.getAuto()
                    + "" + item.getAutuador());
            
        }
    }

    /**
     * @param em
     * @param item
     * @param wrapper
     * @throws AppException 
     */
    private void validarMotivoPenalidadeQuantidadePontos(DadosInfracaoPAD item, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        
        if (!MotivoPenalidadeEnum.QTD_PONTOS.equals(wrapper.getApoioOrigemInstauracao().getMotivo())) {
            throw new RegraNegocioException("Motivo penalidade não é Suspensão."
                    + "" + item.getIsn()
                    + "" + item.getCpfCondutor().getCpf()
                    + "" + item.getAuto()
                    + "" + item.getAutuador());
        }
    }

    private String getValorPenal(EntityManager em, ApoioOrigemInstauracao apoioOrigemInstauracao, Integer pontuacao) throws AppException {
        ValorReferenciaLegal referencia = new ValorReferenciaLegalRepositorio().getValorPenalPorPontuacao(em, apoioOrigemInstauracao, pontuacao);
        
        return referencia.getValor();
    }
}
