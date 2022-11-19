package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.ResultadoLaudoSituacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.PostoAtendimento;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.ImpressaoProva;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.core.iface.servico.adm.IMunicipioService;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP08;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP08BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PADocumentoPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoProvaPARepositorio;
import br.gov.ms.detran.processo.administrativo.dto.DtnIntraPAAtendimento;
import br.gov.ms.detran.processo.administrativo.dto.DtnIntraPAPessoa;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoInstauracaoService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoProvaPA;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.util.SequencialRESTfulService;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PADocumentoPessoaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoXmlWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacaoProvaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteRetornoWSWrapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author desenvolvimento
 */
public class ProcessoAdministrativoBO extends FabricaJSPASequencial {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBO.class);

    IHabilitacaoService habilitacaoService;
    IApoioService apoioService;
    IAcessoService acessoService;
    IMunicipioService municipioService;

    IProcessoAdministrativoService processoAdministrativoService;
    IPAControleFalhaService paControleFalha;
    IExecucaoInstauracaoService execucaoService;

    /**
     *
     * @return
     */
    IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }
    
     /**
     * @return
     */
    IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }
    
    /**
     * 
     * @return 
     */
    IHabilitacaoService getHabilitacaoService() {
        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }
        return habilitacaoService;
    }

    /**
     * 
     * @return 
     */
    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }

    /**
     * 
     * @return 
     */
    IExecucaoInstauracaoService getExecucaoService() {
        if (execucaoService == null) {
            execucaoService = ServiceJndiLocator.<IExecucaoInstauracaoService>lookup("ejb/ExecucaoInstauracaoService");
        }
        return execucaoService;
    }

    /**
     *
     * @return
     */
    private IMunicipioService getMunicipioService() {

        if (municipioService == null) {
            municipioService = (IMunicipioService) JNDIUtil.lookup("ejb/MunicipioService");
        }
        return municipioService;
    }

    /**
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     * @throws DatabaseException 
     */
    public ProcessoAdministrativo gravar(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException, DatabaseException {

        ProcessoAdministrativo processoAdministrativo = new ProcessoAdministrativo();

        processoAdministrativo.setExecucaoInstauracao(
            getExecucaoService().getExecucaoInstauracao(wrapper.getExecucaoInstauracaoId())
        );

        processoAdministrativo.setCnh(wrapper.getDadosCondutorPAD().getNumeroCnh());
        processoAdministrativo.setCpf(wrapper.getDadosCondutorPAD().getCpf());
        processoAdministrativo.setNumeroPermissionado(wrapper.getDadosCondutorPAD().getNumeroPermissionado());
        processoAdministrativo.setNumeroRegistro(wrapper.getDadosCondutorPAD().getNumeroRegistro());
        processoAdministrativo.setTipo(wrapper.getApoioOrigemInstauracao().getResultadoTipoProcesso());
        processoAdministrativo.setDataProcessamento(Calendar.getInstance().getTime());
        processoAdministrativo.setOrigemApoio(wrapper.getApoioOrigemInstauracao());
        processoAdministrativo.setOrigem(OrigemEnum.PONTUACAO);

        /** Número Detran. **/
        processoAdministrativo.setNumeroDetran(getNumeroDetran(em, wrapper.getDadosCondutorPAD()));

        //IUltimoSequencialService service = (IUltimoSequencialService) JNDIUtil.lookup("ejb/UltimoSequencialService");

        /** Número Processo. **/
        //processoAdministrativo.setNumeroProcesso(service.getNumeroProcesso());
        processoAdministrativo.setNumeroProcesso(new SequencialRESTfulService().getNumeroProcesso());

        /** Número Portaria. **/
        //processoAdministrativo.setNumeroPortaria(service.getNumeroPortaria());
        processoAdministrativo.setNumeroPortaria(new SequencialRESTfulService().getNumeroPortaria());

        /** Atendimento. **/
        try{
            
            Long atendimentoId = gravarAtendimento(processoAdministrativo);
            processoAdministrativo.setAtendimento(atendimentoId);
            
        }catch(AppException e ){
            String falha = "Erro ao gravar atendimento para o cpf  "
                    + "" + processoAdministrativo.getCpf()
                    + "| numero detran:" + processoAdministrativo.getNumeroDetran();

            getControleFalha().gravarFalhaEspecifica(processoAdministrativo.getCpf(), falha, "INSTAURACAO_ERRO_ATENDIMENTO");

            throw new AppException(e);
        }

        return new ProcessoAdministrativoRepositorio().insert(em, processoAdministrativo);
    }

    /**
     *
     * @param cpf
     * @return
     * @throws AppException
     */
    private Long getPessoaCondutor(EntityManager em, String cpf) throws AppException {
        return new PAPessoaRepositorio().buscarNumeroDetranPessoaPeloCPF(em, cpf);
    }

    /**
     * 
     * @param dadosCondutorPAD
     * @return
     * @throws AppException 
     */
    private Long gravarPessoa(DadosCondutorPAD dadosCondutorPAD) throws AppException {

         return new DtnIntraPAPessoa().gravarPessoa(
            dadosCondutorPAD.getCpf(), dadosCondutorPAD.getNomeCondutor(), 
            (dadosCondutorPAD.getSexo() != null ? dadosCondutorPAD.getSexo().ordinal() : null)
        );
    }

    /**
     * 
     * @param dadosCondutorPAD
     * @return
     * @throws AppException 
     */
    private Long getNumeroDetran(EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws AppException {
        
        if(dadosCondutorPAD == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutorPAD.getCpf())) {
            DetranWebUtils.applicationMessageException("Condutor inválido.");
        }
        
        Long numeroDetran = getPessoaCondutor(em, dadosCondutorPAD.getCpf());

        if (numeroDetran == null) {
            numeroDetran = gravarPessoa(dadosCondutorPAD);
        }
        
        if (numeroDetran == null) {
            DetranWebUtils.applicationMessageException("Número Detran inválido.");
        }
        
        return numeroDetran;
    }

    /**
     * 
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public Long gravarAtendimento(ProcessoAdministrativo processoAdministrativo) throws AppException {
        return new DtnIntraPAAtendimento().gravarAtendimento(processoAdministrativo.getNumeroDetran());
    }

    /**
     * @param em
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoDesistenteRetornoWSWrapper> getProcessoAdministrativoDesistentes(EntityManager em) throws AppException {
        
        List<ProcessoAdministrativoDesistenteRetornoWSWrapper> desistentes = new MovimentoCnhRepositorio().getDesistentes(em);
        
        for (ProcessoAdministrativoDesistenteRetornoWSWrapper desistente : desistentes) {
            
            try {
                
                CnhSituacaoEntrega cnhSituacaoEntrega = 
                        (CnhSituacaoEntrega) getApoioService()
                                .getSituacaoEntregaCnh(
                                        desistente.getCnhControle(),
                                        AcaoEntregaCnhEnum.ENTREGA);
                desistente.setObservacao(cnhSituacaoEntrega.getTemplateProtocolo().getObservacao());

                PADocumentoPessoaWrapper documentoWrapper = 
                        new PADocumentoPessoaRepositorio()
                                .getCpfDocumentoPessoa(em, cnhSituacaoEntrega.getUsuario().getPessoa().getId());

                if (documentoWrapper != null) {
                    desistente.setCpfUsuario(documentoWrapper.getNumeroDocumento());
                    desistente.setNomeUsuario(documentoWrapper.getNomePrincipalPessoa());
                }
                
            } catch (Exception e) {
                LOG.error("Erro ao buscar informações de desistente");
                getControleFalha().gravarFalhaEspecifica(desistente.getCpfCondutor(), "Erro ao montar info desistentes", "WS_DESISTENTES: " + e);
            }
        }

        return desistentes;
    }

    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List<InformacaoProvaWrapper> getInformacoesProva(EntityManager em) throws AppException {
        
        List<InformacaoProvaWrapper> informacoesProva = new ArrayList();
        
        List<Integer> andamentos
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_PROVA_CURSO
                );
        
        List<ProcessoAdministrativo> lProcessoAdministrativo
            = new ProcessoAdministrativoRepositorio()
                .getListaProcessoAdministrativoPorAndamentoIniciado(em, andamentos);

        if (!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo)) {

            for (ProcessoAdministrativo processoAdministrativo : lProcessoAdministrativo) {
                
                InformacaoProvaWrapper informacaoProvaWrapper = getInformacoesProva(em, processoAdministrativo);
                
                if(informacaoProvaWrapper != null) {
                    informacoesProva.add(informacaoProvaWrapper);
                }
            }
        }

        return informacoesProva;
    }

    /**
     * @param wrapper
     * @return 
     */
    public Object montarProcessoAdministrativoXml(ProcessoAdministrativoWrapper wrapper) {
        
        ProcessoAdministrativoXmlWrapper wrapperXml = new ProcessoAdministrativoXmlWrapper();
        
        wrapperXml.setCpf(wrapper.getEntidade().getCpf());
        wrapperXml.setCnh(wrapper.getEntidade().getCnh());
        wrapperXml.setTipoProcesso(wrapper.getTipoLabel());
        wrapperXml.setNumeroDetran(wrapper.getEntidade().getNumeroDetran());
        wrapperXml.setNumeroProcesso(new NumeroAnoAdapter().marshal(wrapper.getEntidade().getNumeroProcesso()));
        wrapperXml.setNumeroPortaria(wrapper.getEntidade().getNumeroPortaria());
        wrapperXml.setAndamento(wrapper.getDescricaoAndamento());
        wrapperXml.setDataProcessamento(wrapper.getEntidade().getDataProcessamento());
        wrapperXml.setSituacao(wrapper.getSituacaoLabel());
        
        return wrapperXml;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return 
     */
    public InformacaoProvaWrapper getInformacoesProva(EntityManager em, ProcessoAdministrativo processoAdministrativo) {

        InformacaoProvaWrapper wrapper = null;

        try {

            String numeroProcesso       = processoAdministrativo.getNumeroProcesso();
            String descricaoResultado   = "";
            String local                = "";
            BigDecimal nota             = null;
            Date dataHoraProva          = null;

            ResultadoProvaPA resultadoProvaPA = 
                new ResultadoProvaPARepositorio()
                    .getResultadoProvaPAAtivoPorProcessoAdministrativo(em, processoAdministrativo);

            if(resultadoProvaPA != null) {

                if (resultadoProvaPA.getResultadoLaudoSituacao() == null) {
                    
                    descricaoResultado = "APTO";
                    dataHoraProva = resultadoProvaPA.getDataProva();

                    Municipio municipio 
                        = (Municipio) getApoioService().getMunicipioId(resultadoProvaPA.getMunicipio());
                    
                    if (municipio != null) {
                        local = "NACIONAL - " + municipio.getNome().trim();
                    }

                } else {
                    
                    ResultadoLaudoSituacao resultadoLaudoSituacao
                        = (ResultadoLaudoSituacao) getHabilitacaoService()
                            .getResultadoLaudoSituacaoPorId(resultadoProvaPA.getResultadoLaudoSituacao());

                    if (resultadoLaudoSituacao != null) {

                        ImpressaoProva impressaoProva
                            = (ImpressaoProva) getHabilitacaoService()
                                .getImpressaoProvaPorNumeroDetranEExame(
                                    processoAdministrativo.getNumeroDetran(),
                                    resultadoLaudoSituacao.getExame().getExame().getId()
                                );

                        //Resultado: Agt_Descricao (TDC - TDY - AEC - AEB - AGU - AGT)
                        descricaoResultado = resultadoLaudoSituacao.getExame().getResultado().getTipoResultadoExame().getDescricao();

                        //Nota (nota): Abl_Nota_Prova (TDC - TDY - AEC - AEB - AEA - ABL)
                        nota = impressaoProva.getProva().getNota();

                        //Data da Prova (data): Aea_Data_Exame (TDC - TDY - AEC - AEB - AEA)
                        String dataProvaHoraStr
                                = (Utils.formatDate(impressaoProva.getDataProva(), "dd/MM/yyyy")
                                + " "
                                + Utils.formatDate(impressaoProva.getHoraProva(), "HH:mm"));

                        dataHoraProva = Utils.getDate(dataProvaHoraStr, "dd/MM/yyyy HH:mm");

                        //Posto de Atendimento (local): Xae_Descricao (TDC - TDY - AEC - AEB - AEA - AAA - AAB - AAI - ABW - ABY - AAH - XAE)
                        PostoAtendimento postoAtendimento
                            = (PostoAtendimento) getApoioService()
                                .buscarPostoAtendimentoPorId(Long.valueOf(impressaoProva.getPostoAtendimento()));
                        
                        local = postoAtendimento.getDescricao();
                    }
                    
                }
                wrapper
                    = new InformacaoProvaWrapper(
                        numeroProcesso,
                        descricaoResultado,
                        nota,
                        dataHoraProva,
                        local
                    );
            }

        } catch (Exception e) {
            
//            LOG.error("Erro capturado.", e);
            
            getControleFalha()
                .gravarFalhaCondutor(
                    e, 
                    "Erro ao obter informações da prova", 
                    processoAdministrativo.getCpf()
                );
        }

        return wrapper;
    }

    /**
     * @param em
     * @param pa
     * @throws AppException 
     */
    public void executarAndamento48paraEntregaCnh(EntityManager em, ProcessoAdministrativo pa) throws AppException {
        
        if (null != pa) {
            
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            
            new ExecucaoAndamentoManager().iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(pa, usuarioDetran.getId(), null, null));
                
        }
    }
    
    public List getCodesAndamentoProcessoPA() throws AppException {
        List<Integer> codigos = 
            Arrays.asList(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA,
                          PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.PROCESSO_ARQUIVADO_PENA_CUMPRIDA,
                          PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO,
                          PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.PROCESSO_ARQUIVADO_RECURSO_PROVIDO,
                          PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO,
                          PAAndamentoProcessoConstante.ARQUIVAMENTO.PROCESSO_ARQUIVADO_MANUALMENTE,
                          PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB,
                          82,94,95,99,101,105,107);
        return codigos;
    }
}