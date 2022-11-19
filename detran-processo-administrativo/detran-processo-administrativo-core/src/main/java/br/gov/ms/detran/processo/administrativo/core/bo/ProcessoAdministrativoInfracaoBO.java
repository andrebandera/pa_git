package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.Infracao;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP89BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracaoSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAMunicipioWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSInfracaoDespachoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoInfracaoBO {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoInfracaoBO.class);
    
    private IApoioService apoioService;
    /**
     * @return
     */
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public ProcessoAdministrativoInfracaoBO() {
    }

    /**
     *
     * @param em
     * @param processoAdministrativo
     * @param conjuntoInfracoesPA
     * @param apoio
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws AppException
     */
    public void geraInfracao(
        EntityManager em, 
        ProcessoAdministrativo processoAdministrativo, 
        List<DadosInfracaoPAD> conjuntoInfracoesPA, 
        ApoioOrigemInstauracao apoio) throws RegraNegocioException, AppException {

        LOG.info("INÍCIO - Instauração -> Infração.");

        if (!DetranCollectionUtil.ehNuloOuVazio(conjuntoInfracoesPA)) {

            RegrasGeralInstauracaoInfracaoBO regraGeralInfracaoBO = new RegrasGeralInstauracaoInfracaoBO();
            
            for (DadosInfracaoPAD dadosInfracaoPAD : conjuntoInfracoesPA) {

                regraGeralInfracaoBO.regraInfracaoValida(em, dadosInfracaoPAD);

                ProcessoAdministrativoInfracao infracao = new ProcessoAdministrativoInfracao();

                infracao.setAutoInfracao(dadosInfracaoPAD.getAuto());
                infracao.setDataInfracao(dadosInfracaoPAD.getDataInfracao());
                infracao.setIsn(dadosInfracaoPAD.getIsn());
                
                if(dadosInfracaoPAD.getOrigemInfracao() != null) {
                    infracao.setOrigemInfracao(dadosInfracaoPAD.getOrigemInfracao().longValue());
                }
                
                infracao.setOrigemInformacaoPontuacao(dadosInfracaoPAD.getOrigemInformacaoPontuacao());
                infracao.setPlaca(dadosInfracaoPAD.getPlaca());
                infracao.setQuantidadePontosInfracao(dadosInfracaoPAD.getQdePontosInfracao());
                infracao.setStatusPontuacao(dadosInfracaoPAD.getStatusPontuacao());
                infracao.setSituacao(ProcessoAdministrativoInfracaoSituacaoEnum.CADASTRADO);

                if (dadosInfracaoPAD.getAutuador() != null) {
                    infracao.setOrgaoAutuador(
                        new PAApoioRepositorio().getOrgaoAutuadorIdPeloCodigo(em, dadosInfracaoPAD.getAutuador())
                    );
                }

                if (!DetranStringUtil.ehBrancoOuNulo(dadosInfracaoPAD.getInfracaoCodigo())) {

                    Long acaoPenalidadeId = new PAInfracaoRepositorio().
                            getAcaoInfracaoPenalidadePeloCodigoEAmparoLegal(em, 
                                dadosInfracaoPAD.getInfracaoCodigo(),
                                apoio.getAmparoLegal(),
                                apoio.getResultadoAcao().ordinal(),
                                apoio.getAcaoSistema().ordinal(),
                                dadosInfracaoPAD.getDataInfracao()
                            );

                    if (acaoPenalidadeId == null) {
                        Object[] params = {
                            dadosInfracaoPAD.getInfracaoCodigo(),
                            apoio.getAmparoLegal(),
                            apoio.getResultadoAcao(),
                            apoio.getAcaoSistema()

                        };
                        throw new RegraNegocioException(
                                MessageFormat.format("ID ação penalidade não encontrado: CodInfr {0} - AmpLegal {1} - AçãoRes {2} - AçãoSis {3}", params)
                        );
                    }

                    infracao.setInfracao(acaoPenalidadeId);

                    /** Código Infração. **/
                    infracao.setCodigoInfracao(dadosInfracaoPAD.getInfracaoCodigo());
                }
                
                infracao.setProcessoAdministrativo(processoAdministrativo);
                
                new AbstractJpaDAORepository().insert(em, infracao);
            }
        }

        LOG.info("FIM - Instauração -> Infração.");
    }

    /**
     * 
     * Recupera as infrações dos Processos Administrativos instaurados e executa o AEMNPP89.
     * 
     * @param iBaseEntity
     * @throws AppException 
     */
    public void sinalizaInfracaoUtilizada(IBaseEntity iBaseEntity) throws AppException {
        
        if (iBaseEntity == null) {
            DetranWebUtils.applicationMessageException("Infração inválida.");
        }
        
        ProcessoAdministrativoInfracao infracao = (ProcessoAdministrativoInfracao) iBaseEntity;
        
        if (infracao != null 
                && infracao.getIsn() != null 
                && infracao.getProcessoAdministrativo() != null
                && !DetranStringUtil.ehBrancoOuNulo(infracao.getProcessoAdministrativo().getCpf())) {
            
            new AEMNPP89BO()
                .executarIntegracaoAEMNPP89(
                    infracao.getIsn(), 
                    infracao.getProcessoAdministrativo().getCpf(), 
                    infracao.getProcessoAdministrativo().getOrigemApoio().getCodigoConfirmacaoMainFrame(),
                    infracao.getProcessoAdministrativo().getNumeroProcesso()
                );
        }
    }
    
    /**
     * 
     * @param em
     * @param pabpmsWrapper
     * @param iBaseEntity
     * @throws AppException 
     */
    public void atualizarInfracoes(EntityManager em, PABPMSWrapper pabpmsWrapper, IBaseEntity iBaseEntity) throws AppException {

        if(iBaseEntity == null) {
            DetranWebUtils.applicationMessageException("Processo administrativo inválido.");
        }
        
        ProcessoAdministrativo processoAdministrativo = (ProcessoAdministrativo) iBaseEntity;
        
        List<ProcessoAdministrativoInfracao> infracaoPAs
            = new ProcessoAdministrativoInfracaoRepositorio()
                .getInfracoesPorProcessoAdministrativoID(em, processoAdministrativo.getId());
        
        if (DetranCollectionUtil.ehNuloOuVazio(pabpmsWrapper.getDadosInfracaoLocal()) 
            && DetranCollectionUtil.ehNuloOuVazio(pabpmsWrapper.getDadosInfracaoRenainf())
                && DetranCollectionUtil.ehNuloOuVazio(pabpmsWrapper.getDadosInfracao())) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar infrações locais nem Renainf para este processo.");
        }
        
        List<IProcessoAdministrativoInfracao> listaInfracoes = new ArrayList<>();
        listaInfracoes.addAll(pabpmsWrapper.getDadosInfracaoLocal());
        listaInfracoes.addAll(pabpmsWrapper.getDadosInfracaoRenainf());
        listaInfracoes.addAll(pabpmsWrapper.getDadosInfracao());

        for (ProcessoAdministrativoInfracao infracaoPAD : infracaoPAs) {
            Collection<IProcessoAdministrativoInfracao> encontradas = 
                    Collections2.filter(listaInfracoes, getPredicadoInfracao(infracaoPAD));
            
            if(DetranCollectionUtil.ehNuloOuVazio(encontradas)){
                DetranWebUtils.applicationMessageException("Não foi possível encontrar a infração {0} nas buscas da base local e Renainf.", "", infracaoPAD.getAutoInfracao());
            }
            
            atualizarInfracao(em, infracaoPAD, encontradas.iterator().next());
        }
        
        PAOcorrenciaStatus ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        
        if (ocorrenciaAtual == null || 
                !PAAndamentoProcessoConstante.INSTAURACAO.ATUALIZAR_INFRACOES_INSTAURACAO
                        .equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
            
            DetranWebUtils.applicationMessageException("O andamento do Processo Administrativo é diferente do Andamento informado.");
        }
                
    }

    /**
     * @param infracaoPA
     * @return 
     */
    private Predicate<IProcessoAdministrativoInfracao> getPredicadoInfracao(ProcessoAdministrativoInfracao infracaoPA) {
        final String auto = infracaoPA.getAutoInfracao();
        
        Predicate<IProcessoAdministrativoInfracao> predicate = new Predicate<IProcessoAdministrativoInfracao>() {
            @Override
            public boolean apply(IProcessoAdministrativoInfracao t) {
                return t.getAutoInfracao().trim().equalsIgnoreCase(auto.trim());
            }
        };
        return predicate;
    }

    /**
     * 
     * @param em
     * @param infracaoPA
     * @param infracao
     * 
     * @throws AppException 
     */
    private void atualizarInfracao(EntityManager em, ProcessoAdministrativoInfracao infracaoPA, IProcessoAdministrativoInfracao infracao) throws AppException {

        PAMunicipioWrapper municipio = 
                new PAApoioRepositorio()
                        .getMunicipioPeloCodigo(
                            em, infracao.getMunicipioInfracao()
                        );

        infracaoPA.setMunicipio(municipio == null? null : municipio.getId());
        infracaoPA.setLocal(infracao.getLocal());

        if(infracao.getDataInfracao() != null) {
            infracaoPA.setDataInfracao(infracao.getDataInfracao());
        }

        new AbstractJpaDAORepository().update(em, infracaoPA);
    }
    
    /**
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<DadosPABPMSInfracaoDespachoWrapper> geraRegistroInfracoesDespacho(EntityManager em, String numeroProcesso) throws AppException {
        
        List<DadosPABPMSInfracaoDespachoWrapper> lista = new ArrayList<>();
        
        ProcessoAdministrativo pa = new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorNumeroProcessoSemAtivo(em, numeroProcesso);
        
        List<ProcessoAdministrativoInfracao> infracoes = 
                new ProcessoAdministrativoInfracaoRepositorio().getInfracoesPorProcessoAdministrativo(em, pa.getId());
        
        if (!DetranCollectionUtil.ehNuloOuVazio(infracoes)) {
            
            for (ProcessoAdministrativoInfracao infracao : infracoes) {
                
                DadosPABPMSInfracaoDespachoWrapper dados = new DadosPABPMSInfracaoDespachoWrapper();
                
                Infracao infracaoMaa = 
                        (Infracao) getApoioService().getInfracaoPorAcaoInfracaoPenalidadeId(infracao.getInfracao());
                
                if (null != infracaoMaa) {
                    
                    dados.setArtigo(infracaoMaa.getAmparoLegal().getArtigo());
                    dados.setParagrafo(infracaoMaa.getAmparoLegal().getParagrafo()); 
                    dados.setInciso(infracaoMaa.getAmparoLegal().getInciso());
                    dados.setDescricao(infracaoMaa.getDescricao());
                    dados.setCodigo(infracaoMaa.getCodigo());
                    
                    lista.add(dados);
                }
            }
        }
        
        return lista;
    }
}   