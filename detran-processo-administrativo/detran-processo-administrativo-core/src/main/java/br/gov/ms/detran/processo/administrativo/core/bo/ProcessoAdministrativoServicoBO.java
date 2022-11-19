package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.OrgaoBca;
import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.core.projeto.entidade.vei.OrgaoJudicial;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP90;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP90A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP92;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP92A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP94;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP94A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP95;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP95A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP96;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP96A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP97;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP97A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP98;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP98A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP99;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP99A;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP90BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP92BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP93BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP94BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP95BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP96BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP97BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP98BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP99BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoAgravamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoApensadoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoJsonRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialDelitoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoEspecificaProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJuridicoDelito;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PASituacaoJsonEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoAgravamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoApensado;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAmparoLegalWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSArtigoDespachoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSCondutorWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSDadosPontuacaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSDetalhesAutuacaoInfracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSErroWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSGeralWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSHistoricoPontuacaoNomeWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSInfracaoLocalWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSInfracaoRenainfWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSInfracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSJuridicoArtigoDelitoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSJuridicoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSRecursosInfracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSRenavamWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoServicoBO {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoServicoBO.class);

    private static final Integer TAMANHO_EXTRATO = 14;
    
    private static final Long ORIGEM_INFRACAO_LOCAL = 1L;

     IProcessoAdministrativoService processoAdministrativoService;
    IExecucaoEspecificaProcessoAdministrativoService execucaoEspecificaService;
    IPAControleFalhaService paControleFalha;
    
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

    /**
     *
     * @return
     */
    IExecucaoEspecificaProcessoAdministrativoService getExecucaoEspecificaService() {
        if (execucaoEspecificaService == null) {
            execucaoEspecificaService = (IExecucaoEspecificaProcessoAdministrativoService) JNDIUtil.lookup("ejb/ExecucaoEspecificaProcessoAdministrativoService");
        }
        return execucaoEspecificaService;
    }

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
     * @param em
     * @param processo
     * @return 
     * @throws AppException
     */
    public PABPMSWrapper geraEgravaArquivoJson(EntityManager em, ProcessoAdministrativo processo) throws AppException {

        if (processo == null) {
            DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
        }

        List<ProcessoAdministrativoInfracao> infracoes
            = new ProcessoAdministrativoInfracaoRepositorio()
                .getExtratoEAutuadorECodigoInfracao(em, processo.getId());

        List<String> extratos   = new ArrayList();
        List<String> autuadores = new ArrayList();

        montarDadosInfracoes(em, infracoes, extratos, autuadores);

        if (DetranCollectionUtil.ehNuloOuVazio(extratos) || DetranCollectionUtil.ehNuloOuVazio(autuadores)) {
            DetranWebUtils.applicationMessageException("Infrações inválidas para Processo Administrativo.");
        }

        PABPMSWrapper pabpmsWrapper = new PABPMSWrapper();

        /*** INÍCIO OBRIGATÓRIOS. ***/
        
        pabpmsWrapper.setIdProcessoAdministrativo(processo.getId());
        
        /** Dados Geral. **/
        pabpmsWrapper.setDadosGeral(geraDadosGeral(processo));

        /** Dados Condutor. **/
        pabpmsWrapper.setDadosCondutor(geraRegistroCondutor(processo));

        /** Dados PA. **/
        pabpmsWrapper
            .setDadosPA(
                geraDadosPA(
                    em, 
                    processo, 
                    pabpmsWrapper.getDadosCondutor(), 
                    pabpmsWrapper.getDadosGeral().getNumeroPortaria()
                )
            );
        
        /*** FIM OBRIGATÓRIOS. ***/
        
        /** Artigo Despacho. **/
        pabpmsWrapper.setArtigoDespacho(geraArtigoDespacho(em, processo, pabpmsWrapper.getDadosCondutor(), infracoes.get(0)));
        
        if(processo.isPontuacao()) {

            geraDadosPAOrigemPontucao(pabpmsWrapper, processo, extratos, autuadores, infracoes);

        } else if (processo.isJuridico()) {

            /**
             * Dados Juridicos (PJU). *
             */
            pabpmsWrapper.setDadosJuridicos(geraDadosJuridicos(em, processo));

        }

        /** Json. **/
        gravarArquivoJson(em, processo, pabpmsWrapper);
        
        return pabpmsWrapper;
    }

    private void geraDadosPAOrigemPontucao(PABPMSWrapper pabpmsWrapper, ProcessoAdministrativo processo, List<String> extratos, List<String> autuadores, List<ProcessoAdministrativoInfracao> infracoes) throws AppException {
       
         /** Dados Infração. **/
        pabpmsWrapper.setDadosInfracao(geraRegistroInfracoes(processo, extratos));
        
        /** Dados Historico Pontuacao Nome. **/
        pabpmsWrapper.setDadosHistoricoPontuacaoNome(gerarHistoricoPontuacaoNome(processo));

        /** Dados Infracao Local. **/
        pabpmsWrapper.setDadosInfracaoLocal(gerarDadosInfracaoLocal(processo, extratos, autuadores));

        /** Histórico Pontuação. **/
        pabpmsWrapper.setDadosPontuacao(geraHistoricoPontuacao(processo, pabpmsWrapper.getDadosCondutor()));

        /** Dados Recurso Infração. **/
        pabpmsWrapper.setDadosRecursoPontuacao(geraRegistroRecursoInfracoes(processo, infracoes));

        executaAEMNPP98PorBlocos(pabpmsWrapper, processo, extratos, autuadores);
        
        /** Detalhes Autuação Infração. **/
        pabpmsWrapper
                .setDadosDetalhesAutuacaoInfracao(geraDetalhesAutuacaoInfracaoWrapper(pabpmsWrapper.getDadosCondutor(), infracoes));
    }

    private void montarDadosInfracoes(EntityManager em, 
                                      List<ProcessoAdministrativoInfracao> infracoes, 
                                      List<String> extratos, 
                                      List<String> autuadores) throws DatabaseException {
        
        for (ProcessoAdministrativoInfracao infracao : infracoes) {
            
            /** Extratos. **/
            extratos.add(
                    DetranStringUtil.preencherEspaco(
                            DetranStringUtil.preencherEspaco(infracao.getAutoInfracao(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO).concat(infracao.getCodigoInfracao()),
                    TAMANHO_EXTRATO,
                    DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO
                    )
            );
            
            /** Autuadores. **/
            Integer autuador = new PAApoioRepositorio().getOrgaoAutuadorPeloID(em, infracao.getOrgaoAutuador());
            autuadores.add(autuador == null ? "" : autuador.toString());
        }
    }

    /**
     *
     * @param processo
     * @return
     */
    public DadosPABPMSGeralWrapper geraDadosGeral(ProcessoAdministrativo processo) {

        DadosPABPMSGeralWrapper dadosGeral = new DadosPABPMSGeralWrapper();

        dadosGeral.setTipoRegistro(TipoRegistroBPMSEnum.DADOS_GERAL.getCodigo());
        dadosGeral.setNumeroProcesso(processo.getNumeroProcesso());
        dadosGeral.setNumeroPortaria(processo.getNumeroPortaria());
        dadosGeral.setDescricaoTipoProcesso(processo.getTipo().name());
        dadosGeral.setCpfCondutor(processo.getCpf());

        return dadosGeral;
    }

    /**
     *
     * @param em
     * @param processo
     * @param dadosCondutor
     * @param numeroPortaria
     * @return
     * @throws AppException
     */
    public DadosPABPMSWrapper geraDadosPA(EntityManager em, ProcessoAdministrativo processo, 
        DadosPABPMSCondutorWrapper dadosCondutor, String numeroPortaria) throws AppException {

        DadosPABPMSWrapper dadosPa = new DadosPABPMSWrapper();

        if (processo.getOrigemApoio() != null) {
            dadosPa.setDescricaoMotivo(processo.getOrigemApoio().getResultadoMotivo().name());
            dadosPa.setArtigoInciso(processo.getOrigemApoio().getArtigoInciso());
        }

        /** Pendente - Recuperar dados do BD - APOIO. **/
        String quantidadeMesesPenalidade = "00";

        if (!RegraInstaurarEnum.C1.equals(processo.getOrigemApoio().getRegra())) {
            
            PAComplemento complemento = new PAComplementoRepositorio().getComplementoPorPA(em, processo.getId());
            
            quantidadeMesesPenalidade 
                = complemento == null? 
                    "FALHA" : 
                    DetranStringUtil.preencherEspaco(complemento.getValor(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO);
        }

//        dadosPa.setQuantidadeMesesPenalidade(quantidadeMesesPenalidade);
        dadosPa.setValorPrazo(quantidadeMesesPenalidade);
        dadosPa.setUnidadePenalidade(processo.isJuridico()? UnidadePenalEnum.DIA.name(): UnidadePenalEnum.MES.name());

        if (dadosCondutor != null) {
            dadosPa.setNomeCondutor(dadosCondutor.getNomeCondutor());
            dadosPa.setNumeroRegistro(dadosCondutor.getNumeroRegistro());
        }

        PAOcorrenciaStatus ocorrencia
            = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processo.getId());

        if (ocorrencia == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar status andamento ativo.");
        }

        Integer pontuacao
            = new ProcessoAdministrativoInfracaoRepositorio().getSomaPontuacaoDeInfracoesPA(em, processo.getId());

        if (pontuacao == null) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar a soma das pontuações do PA.");
        }

        dadosPa.setDataCadastro(processo.getDataProcessamento());
        dadosPa.setDescricaoAndamento(ocorrencia.getStatusAndamento().getAndamentoProcesso().getDescricao());
        dadosPa.setDescricaoStatus(ocorrencia.getStatusAndamento().getStatus().getDescricao());
        dadosPa.setNumeroCnh(processo.getCnh());
        dadosPa.setQtdPontosProcesso(pontuacao);
        dadosPa.setOrigem(processo.getOrigem().name());

        if (!DetranStringUtil.ehBrancoOuNulo(numeroPortaria)) {
            dadosPa.setAnoPortaria(numeroPortaria.substring(0, 4));
        }

        PAAmparoLegalWrapper amparoLegal = 
                new PAInfracaoRepositorio().
                        getAmparoLegalComArtigoIncisoParagrafoPorId(
                            em, processo.getOrigemApoio().getAmparoLegal()
                        );

        if (null != amparoLegal) {
            dadosPa.setArtigo(amparoLegal.getArtigo());
            dadosPa.setInciso(amparoLegal.getInciso());
            dadosPa.setParagrafo(amparoLegal.getParagrafo());
        }

        dadosPa.setAcaoSistema(processo.getOrigemApoio().getAcaoSistema());
        
        if(AcaoSistemaPAEnum.NOVO_APENSADO.equals(processo.getOrigemApoio().getAcaoSistema())) {
            
            ProcessoAdministrativoApensado apensado 
                = new ProcessoAdministrativoApensadoRepositorio().getProcessoAdministrativoOrigemPorApensado(em, processo);
            
            if(apensado != null 
                    && apensado.getProcessoAdministrativo() != null
                    && !DetranStringUtil.ehBrancoOuNulo(apensado.getProcessoAdministrativo().getNumeroProcesso())) {
            
                dadosPa.setNumeroProcessoOrigem(apensado.getProcessoAdministrativo().getNumeroProcesso());
            }
        
        } else if(AcaoSistemaPAEnum.NOVO_AGRAVADO.equals(processo.getOrigemApoio().getAcaoSistema())) {
            
            ProcessoAdministrativoAgravamento agravamento
                = new ProcessoAdministrativoAgravamentoRepositorio().getProcessoAdministrativoOrigemPorAgravamento(em, processo);
            
            if(agravamento != null 
                    && agravamento.getProcessoAdministrativo() != null
                    && !DetranStringUtil.ehBrancoOuNulo(agravamento.getProcessoAdministrativo().getNumeroProcesso())) {
            
                dadosPa.setNumeroProcessoOrigem(agravamento.getProcessoAdministrativo().getNumeroProcesso());
            }
            
            dadosPa.setNumeroProcessoOrigem(numeroPortaria);
        }
        
        return dadosPa;
    }

    /**
     * @param processo
     * @return
     * @throws AppException
     */
    private List<DadosPABPMSRenavamWrapper> geraDadosRenavam(ProcessoAdministrativo processo, AEMNPP98 aemnpp98) throws AppException {

        List<DadosPABPMSRenavamWrapper> listaDadosRenavam = new ArrayList();
        
        try {

            for (Object object : aemnpp98.getInformacoesExtratos()) {

                AEMNPP98A aemnpp98a = (AEMNPP98A) object;

                AEMNPP99 aemnpp99
                        = new AEMNPP99BO().executarIntegracaoAEMNPP99(
                            DetranStringUtil.preencherEspaco(aemnpp98a.getNumeroAuto(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO).concat(aemnpp98a.getCodigoInfracao()),
                            aemnpp98a.getOrgaoAutuador(),
                            aemnpp98a.getUfAutuador(),
                            aemnpp98a.getPlacaVeiculo(),
                            aemnpp98a.getVeiculoUf()
                        );

                if (!DetranCollectionUtil.ehNuloOuVazio(aemnpp99.getInformacoesExtratos())) {

                    for (Object object2 : aemnpp99.getInformacoesExtratos()) {

                        AEMNPP99A aemnpp99a = (AEMNPP99A) object2;

                        listaDadosRenavam
                            .add(
                                new DadosPABPMSRenavamWrapper(
                                    aemnpp99a.getCodigoTransacao(),
                                    aemnpp99a.getDescricaoTransacao(),
                                    aemnpp99a.getCodigoRetorno(),
                                    aemnpp99a.getProjetoNatural(),
                                    Utils.getDate(aemnpp99a.getDataTransacao().concat(" ").concat(aemnpp99a.getHoraTransacao()), "dd/MM/yyyy HH:mm"),
                                    aemnpp99a.getSituacaoTransacao()
                                )
                            );
                    }
                }
            }
            
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar dados Renavam (AEMNPP99)", processo.getCpf());
        }

        return listaDadosRenavam;
    }

    /**
     *
     * @param processo
     * @param infracoesPA
     * @return
     * @throws AppException
     */
    private List<DadosPABPMSInfracaoWrapper> geraRegistroInfracoes(ProcessoAdministrativo processo, 
                                                                   List<String> infracoesPA) throws AppException {

        List<DadosPABPMSInfracaoWrapper> infracoes = new ArrayList();
        List<Object> informacoesExtratos = new ArrayList<>();

        List<List<String>> blocosInfracoes = Lists.partition(infracoesPA, 25);

        for (List<String> bloco : blocosInfracoes) {
            AEMNPP90 retorno = new AEMNPP90BO().executarIntegracaoAEMNPP90(processo.getCpf(), bloco);
            if (retorno == null || DetranCollectionUtil.ehNuloOuVazio(retorno.getInformacoesExtratos())) {
                DetranWebUtils.applicationMessageException("Não foi possível recuperar AEMNPP90.");
            }
            informacoesExtratos.addAll(retorno.getInformacoesExtratos());
        }

        infracoes.addAll(montarListaDadosInfracao(informacoesExtratos));


        return infracoes;
    }

    /**
     *
     * @param processoAdministrativo
     * @return
     */
    public List<DadosPABPMSDadosPontuacaoWrapper> geraHistoricoPontuacao(
        ProcessoAdministrativo processoAdministrativo, DadosPABPMSCondutorWrapper dadosCondutor) throws AppException {

        List<DadosPABPMSDadosPontuacaoWrapper> lHistoricoPontuacao = new ArrayList();
        
        try {
        
            if (DetranStringUtil.ehBrancoOuNulo(processoAdministrativo.getCpf())) {
                DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
            }

            Integer situacaoPesquisa = 0;
            Long qtdInfracoes = 0L;

            do {

                AEMNPP94 aemnpp94 
                    = new AEMNPP94BO()
                        .executarIntegracaoAEMNPP94(
                            processoAdministrativo.getCpf(),
                            situacaoPesquisa,
                            qtdInfracoes
                        );

                if (aemnpp94 == null || DetranCollectionUtil.ehNuloOuVazio(aemnpp94.getInformacoesInfracoes())) {
                    DetranWebUtils.applicationMessageException("Não foi possível recuperar AEMNPP94.");
                }

                for (Object object : aemnpp94.getInformacoesInfracoes()) {

                    AEMNPP94A aemnpp94a = (AEMNPP94A) object;

                    DadosPABPMSDadosPontuacaoWrapper historicoPontuacao = new DadosPABPMSDadosPontuacaoWrapper();

                    historicoPontuacao.setNumeroAutoInfracao(aemnpp94a.getNumeroExtrato().substring(0, 10));
                    historicoPontuacao.setCodigoInfracao(aemnpp94a.getNumeroExtrato().substring(10));
                    historicoPontuacao.setDataInfracao(Utils.getDate(aemnpp94a.getDataInfracao(), "dd/MM/yyyy"));
                    historicoPontuacao.setNumeroPontos(aemnpp94a.getPontosInfracao());
                    historicoPontuacao.setNomeCondutor(dadosCondutor.getNomeCondutor());
                    historicoPontuacao.setSituacaoPontuacao("1");
                    historicoPontuacao.setDescricaoSituacao("Ativa");

                    lHistoricoPontuacao.add(historicoPontuacao);
                }

                situacaoPesquisa = Integer.valueOf(aemnpp94.getSituacaoPesquisa().trim());
                qtdInfracoes = Long.valueOf(aemnpp94.getQtdeInfracoes().trim());

            } while (situacaoPesquisa == 1);
            
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar historico pontuacao (AEMNPP94)", processoAdministrativo.getCpf());
        }

        return lHistoricoPontuacao;
    }

    /**
     *
     * @param infracoes
     * @return
     * @throws AppException
     */
    private List<String> recuperarExtratosDoPA(List<ProcessoAdministrativoInfracao> infracoes) throws AppException {

        List<String> extratos = new ArrayList<>();

        for (ProcessoAdministrativoInfracao infracao : infracoes) {

            extratos.add(
                    DetranStringUtil.preencherEspaco(
                            infracao.getAutoInfracao().concat(infracao.getCodigoInfracao()),
                            TAMANHO_EXTRATO,
                            DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO
                    )
            );
        }

        return extratos;
    }

    /**
     *
     * @param informacoesExtratos
     * @return
     */
    private List<DadosPABPMSInfracaoWrapper> montarListaDadosInfracao(List informacoesExtratos) {

        List<DadosPABPMSInfracaoWrapper> infracoes = new ArrayList();

        for (Object informacoesExtrato : informacoesExtratos) {
            infracoes.add(parseAEMNPP90AtoWrapper((AEMNPP90A) informacoesExtrato));
        }
        
        return infracoes;
    }

    /**
     *
     * @param aemnpp90a
     * @return
     */
    private DadosPABPMSInfracaoWrapper parseAEMNPP90AtoWrapper(AEMNPP90A aemnpp90a) {

        DadosPABPMSInfracaoWrapper wrapper = new DadosPABPMSInfracaoWrapper();

        wrapper.setCodigoInfracao(aemnpp90a.getCodigoInfracaoDesdobramento());
        wrapper.setDescricaoAutuador(aemnpp90a.getNomeAutuador());
        wrapper.setDataInfracao(Utils.getDate(aemnpp90a.getDataInfracao().concat(" ").concat(aemnpp90a.getHoraInfracao()), "yyyyMMdd HHmm"));
        
        
        wrapper.setNumeroAutoInfracao(aemnpp90a.getNumeroAuto());
        wrapper.setCodigoAutuador(aemnpp90a.getCodigoAutuador());
        wrapper.setCodigoMunicipioInfracao(aemnpp90a.getCodigoMunicipioInfracao());
        wrapper.setDataEmissaoPenalidade(Utils.getDate(aemnpp90a.getDataEmissaoNotificacao(), "yyyyMMdd"));
        wrapper.setDataVencimentoPagamento(Utils.getDate(aemnpp90a.getDataVencimento(), "yyyyMMdd"));
        wrapper.setDescricaoInfracao(aemnpp90a.getDescricaoInfracao());
        wrapper.setLocalInfracao(aemnpp90a.getLocalInfracao());
        wrapper.setNomeMunicipioInfracao(aemnpp90a.getNomeMunicipioInfracao());
        wrapper.setPlaca(aemnpp90a.getPlaca());
        wrapper.setQtdPontosInfracao(Integer.parseInt(aemnpp90a.getQtdePontosInfracao()));

        return wrapper;
    }

    /**
     *
     * @param processo
     * @return
     * @throws AppException
     */
    public DadosPABPMSCondutorWrapper geraRegistroCondutor(ProcessoAdministrativo processo) throws AppException {

        AEMNPP93 aemnpp93 = new AEMNPP93BO().executarIntegracaoAEMNPP93(processo.getCpf());

        if (aemnpp93 == null) {
            DetranWebUtils.applicationMessageException("aemnpp93.executar.exception");
        }

        return parseAEMNPP93toDadosCondutorBPMSWrapper(aemnpp93);
    }

    /**
     *
     * @param aemnpp93
     * @return
     */
    private DadosPABPMSCondutorWrapper parseAEMNPP93toDadosCondutorBPMSWrapper(AEMNPP93 aemnpp93) {

        DadosPABPMSCondutorWrapper wrapper = new DadosPABPMSCondutorWrapper();

        wrapper.setBairro(aemnpp93.getBairro());
        wrapper.setCategoria1Habilitacao(aemnpp93.getCategoriaHabilitacao());
        wrapper.setCategoriaAtualCnh(aemnpp93.getCategoriaAtualCnh());
        wrapper.setCep(aemnpp93.getCep());
        wrapper.setCodigoUltimaTransacao(aemnpp93.getCodigoUltimaTransacao());
        wrapper.setComplemento(aemnpp93.getComplemento());
        wrapper.setCpf(aemnpp93.getCpf());
        wrapper.setData1Habilitacao(Utils.getDate(aemnpp93.getDataPrimeiraHabilitacao(), "ddMMyyyy"));
        wrapper.setDataAtualizacao(Utils.getDate(aemnpp93.getDataAtualizacao(), "ddMMyyyy"));
        wrapper.setDataAtualizacaoBinco(Utils.getDate(aemnpp93.getDataTransacao155(), "ddMMyyyy"));
        wrapper.setDataEmissaoDefinitiva(aemnpp93.getEmissaoDefinitiva());
        wrapper.setDataNascimento(Utils.getDate(aemnpp93.getDataNascimento(), "ddMMyyyy"));
        wrapper.setDataValidadeCnh(Utils.getDate(aemnpp93.getDataValidadeCnh(), "ddMMyyyy"));
        wrapper.setDataValidadeDefinitiva(Utils.getDate(aemnpp93.getDataValidadeCnhDefinitiva(), "ddMMyyyy"));
        wrapper.setDocumento(aemnpp93.getTipoDocumento());
        wrapper.setIndicativoObservacao(aemnpp93.getIndicativoObservacao());
        wrapper.setLogradouro(aemnpp93.getLogradouro());
        wrapper.setNacionalidade(aemnpp93.getNacionalidade());
        wrapper.setNomeCondutor(aemnpp93.getNomeCondutor());
        wrapper.setNomeMae(aemnpp93.getNomeMae());
        wrapper.setNomeMunicipio(aemnpp93.getNomeMunicipio());
        wrapper.setNomeMunicipioNascimento(aemnpp93.getNomeMunicipioNascimento());
        wrapper.setNomePai(aemnpp93.getNomeMunicipioNascimento());
        wrapper.setNumeroCnh(DetranStringUtil.ehBrancoOuNulo(aemnpp93.getNumeroCnh())? null : Long.parseLong(aemnpp93.getNumeroCnh()));
        wrapper.setNumeroPgu(aemnpp93.getNumeroPgu());
        wrapper.setNumeroRegistro(aemnpp93.getNumeroRegistro());
        wrapper.setRenach(aemnpp93.getRenach());
        wrapper.setSexo(aemnpp93.getSexo());
        wrapper.setSituacaoCnh(aemnpp93.getSituacaoCnh());
        wrapper.setSituacaoCpf(aemnpp93.getSituacaoCpf());
        wrapper.setSituacaoSihab(aemnpp93.getSituacaoSihab());
        wrapper.setTelefone(aemnpp93.getTelefone());
        wrapper.setUf1Habilitacao(aemnpp93.getUfPrimeiraHabilitacao());
        wrapper.setUfTransferencia(aemnpp93.getUfTransferencia());

        return wrapper;
    }

    /**
     * @param processo
     * @return
     */
    public List<DadosPABPMSHistoricoPontuacaoNomeWrapper> gerarHistoricoPontuacaoNome(ProcessoAdministrativo processo) throws AppException {

        List<DadosPABPMSHistoricoPontuacaoNomeWrapper> listaDados = new ArrayList();
        
        try {
        
            AEMNPP95 aemnpp95 = new AEMNPP95BO().executarIntegracaoAEMNPP95(processo.getCpf());

            if (aemnpp95 == null || DetranCollectionUtil.ehNuloOuVazio(aemnpp95.getInformacoesExtratos())) {
                DetranWebUtils.applicationMessageException("Não foi possível recuperar AEMNPP95.");
            }

            for (Object object : aemnpp95.getInformacoesExtratos()) {

                AEMNPP95A aemnpp95a = (AEMNPP95A) object;

                listaDados.add(new DadosPABPMSHistoricoPontuacaoNomeWrapper(
                                aemnpp95.getNumeroPgu(),
                                aemnpp95.getNumeroRegistro(),
                                aemnpp95.getNomeCondutor(),
                                aemnpp95a.getNumeroExtrato().substring(0, 10),
                                aemnpp95a.getNumeroExtrato().substring(10),
                                Utils.getDate(aemnpp95a.getDataInfracao(), "dd/MM/yyyy"),
                                Integer.parseInt(aemnpp95a.getQtdePontosInfracao()),
                                Utils.getDate(aemnpp95a.getDataVencimento(), "dd/MM/yyyy"),
                                aemnpp95a.getArtigoInfracao(),
                                aemnpp95a.getOrigemInformacao(),
                                aemnpp95a.getClassificacaoInfracao()
                        )
                );
            }
        
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar historico pontuacao nome (AEMNPP95)", processo.getCpf());
        }

        return listaDados;
    }

    /**
     *
     * @param processo
     * @param infracoes
     * @return
     * @throws AppException
     */
    private List<DadosPABPMSRecursosInfracaoWrapper> geraRegistroRecursoInfracoes(ProcessoAdministrativo processo, List<ProcessoAdministrativoInfracao> infracoes) throws AppException {

        List<DadosPABPMSRecursosInfracaoWrapper> listaRecursos = new ArrayList<>();

        try {
            List<DadosPABPMSRecursosInfracaoWrapper> recursos = new ArrayList();
        
            for (ProcessoAdministrativoInfracao infracao : infracoes) {
                recursos.add(new DadosPABPMSRecursosInfracaoWrapper(infracao.getAutoInfracao(), infracao.getCodigoInfracao()));
            }

            AEMNPP96 retorno = new AEMNPP96BO()
                .executarIntegracaoAEMNPP96(processo.getCpf(), recuperarExtratosDoPA(infracoes));

            if (retorno == null || retorno.getInformacoesExtratos() == null) {
                DetranWebUtils.applicationMessageException("Não foi possível recuperar AEMNPP96.");
            }

            listaRecursos = parseAEMNPP96toWrapper(recursos, retorno.getInformacoesExtratos());
        
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar registro recurso infrações (AEMNPP96)", processo.getCpf());
        }
        
        return listaRecursos;
    }

    /**
     *
     * @param recursos
     * @param informacoesExtratos
     */
    private List<DadosPABPMSRecursosInfracaoWrapper> parseAEMNPP96toWrapper(List<DadosPABPMSRecursosInfracaoWrapper> recursos, List<Object> informacoesExtratos) {

        List<DadosPABPMSRecursosInfracaoWrapper> listaRecursos = new ArrayList<>();
        
        for (int i = 0; i < recursos.size(); i++) {

            AEMNPP96A aemnpp96a = (AEMNPP96A) informacoesExtratos.get(i);

            if(BooleanEnum.SIM.getCodigo()== aemnpp96a.getSituacaoRecurso().charAt(0)){
                
                recursos.get(i).setDescricaoRecurso(aemnpp96a.getDescricaoRecurso());
                recursos.get(i).setRequerente(aemnpp96a.getRequerente());
                recursos.get(i).setSituacaoInfracao(aemnpp96a.getSituacaoInfracao());
                recursos.get(i).setSinteseDefesa(aemnpp96a.getSinteseDefesa());
                recursos.get(i).setObservacaoResultado(aemnpp96a.getObservacao());
                recursos.get(i).setDescricaoResultado(aemnpp96a.getResultadoRecurso());
                listaRecursos.add(recursos.get(i));
            }
        }
        return listaRecursos;
    }

    /**
     * @param processoAdministrativo
     * @return
     * @throws AppException
     */
    private List<DadosPABPMSInfracaoRenainfWrapper> geraDadosInfracaoRenainf(
        ProcessoAdministrativo processoAdministrativo, AEMNPP98 aemnpp98) throws AppException {

        List<DadosPABPMSInfracaoRenainfWrapper> lInfracaoRenainf = new ArrayList();
        
        try {
        
            if (processoAdministrativo == null || DetranStringUtil.ehBrancoOuNulo(processoAdministrativo.getCpf())) {
                DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
            }

            for (Object object : aemnpp98.getInformacoesExtratos()) {

                DadosPABPMSInfracaoRenainfWrapper infracaoRenainf = new DadosPABPMSInfracaoRenainfWrapper();
                
                AEMNPP98A aemnpp98a = (AEMNPP98A) object;

                infracaoRenainf.setDataCadastramento(Utils.getDate(aemnpp98a.getDataCadastramentoInfracao(), "dd/MM/yy"));
                infracaoRenainf.setUfAutuador(aemnpp98a.getUfAutuador());
                infracaoRenainf.setStatusInfracao(aemnpp98a.getStatusInfracao());
                infracaoRenainf.setExigivel(aemnpp98a.getExigibilidadeInfracao());
                infracaoRenainf.setCodigoAutuador(aemnpp98a.getOrgaoAutuador());
                infracaoRenainf.setDescricaoAutuador(aemnpp98a.getDescricaoOrgaoAutuador());
                infracaoRenainf.setNumeroAutoInfracao(aemnpp98a.getNumeroAuto());
                infracaoRenainf.setCodigoRenainf(aemnpp98a.getCodigoRenainfInfracao());
                infracaoRenainf.setCodigoInfracao(aemnpp98a.getCodigoInfracao());
                infracaoRenainf.setDescricaoInfracao(aemnpp98a.getDescricaoInfracao());
                infracaoRenainf.setDataInfracao(Utils.getDate(aemnpp98a.getDataInfracao().concat(" ").concat(aemnpp98a.getHoraInfracao()), "dd/MM/yy HH:mm"));
                infracaoRenainf.setPlacaVeiculo(aemnpp98a.getPlacaVeiculo());
                infracaoRenainf.setCodigoPais(aemnpp98a.getCodigoPais());
                infracaoRenainf.setInformacoesCondutor(aemnpp98a.getMensagemCondutor());
                infracaoRenainf.setLocalInfracao(aemnpp98a.getLocalInfracao());
                infracaoRenainf.setCodigoMunicipio(aemnpp98a.getCodigoMunicipioInfracao());
                infracaoRenainf.setNomeMunicipio(aemnpp98a.getNomeMunicipioInfracao());
                infracaoRenainf.setMedicaoReal(aemnpp98a.getMedicaoReal());
                infracaoRenainf.setLimitePermitido(aemnpp98a.getLimitePermitido());
                infracaoRenainf.setMedicaoConsiderada(aemnpp98a.getMedicaoConsiderada());
                infracaoRenainf.setValorInfracao(aemnpp98a.getValorInfracao());
                infracaoRenainf.setCodigoUnidade(aemnpp98a.getUnidadeMedida());
                infracaoRenainf.setObservacoesGerais(aemnpp98a.getObservacaoInfracao());
                infracaoRenainf.setCodigoCarroceria(aemnpp98a.getCodigoCarroceria());
                infracaoRenainf.setCodigoCategoria(aemnpp98a.getCodigoCategoria());
                infracaoRenainf.setCodigoCorVeiculo(aemnpp98a.getCodigoCorVeiculo());
                infracaoRenainf.setCodigoEspecie(aemnpp98a.getCodigoEspecie());
                infracaoRenainf.setCodigoMarca(aemnpp98a.getCodigoMarca());
                infracaoRenainf.setCodigoMunicipioVeiculo(aemnpp98a.getCodigoMunicipioVeiculo());
                infracaoRenainf.setCodigoOrigemPossuidor(aemnpp98a.getCodigoOrigemPossuidor());
                infracaoRenainf.setCodigoRestricao1(aemnpp98a.getCodigoRestricao1());
                infracaoRenainf.setCodigoRestricao2(aemnpp98a.getCodigoRestricao2());
                infracaoRenainf.setCodigoRestricao3(aemnpp98a.getCodigoRestricao3());
                infracaoRenainf.setCodigoRestricao4(aemnpp98a.getCodigoRestricao4());
                infracaoRenainf.setDataAceiteDesvinculacaoUFOrigem(Utils.getDate(aemnpp98a.getDataAceiteDesvinculacaoUFOrigem(), "dd/MM/yyyy"));
                infracaoRenainf.setDataAceiteNotificacaoAutuacao(Utils.getDate(aemnpp98a.getDataAceiteNotificacaoAutuacao(), "dd/MM/yyyy"));
                infracaoRenainf.setDataAceiteNotificacaoPenalidade(Utils.getDate(aemnpp98a.getDataAceiteNotificacaoPenalidade(), "dd/MM/yyyy"));
                infracaoRenainf.setDataApresentacaoInfrator(Utils.getDate(aemnpp98a.getDataApresentacaoInfrator(), "dd/MM/yyyy"));
                infracaoRenainf.setDataDesvinculacao(Utils.getDate(aemnpp98a.getDataDesvinculacao(), "dd/MM/yyyy"));
                infracaoRenainf.setDataEmissaoNotificacaoAutuacao(Utils.getDate(aemnpp98a.getDataEmissaoNotificacaoAutuacao(), "dd/MM/yyyy"));
                infracaoRenainf.setDataEmissaoNotificacaoPenalidade(Utils.getDate(aemnpp98a.getDataEmissaoNotificacaoPenalidade(), "dd/MM/yyyy"));
                infracaoRenainf.setDataIndicacaoPontuacao(Utils.getDate(aemnpp98a.getDataIndicacaoPontuacao(), "dd/MM/yyyy"));
                infracaoRenainf.setDataLimiteDefesaNotificacaoAutuacao(Utils.getDate(aemnpp98a.getDataLimiteDefesaNotificacaoAutuacao(), "dd/MM/yyyy"));
                infracaoRenainf.setDataNotificacaoAutuacaoEdital(Utils.getDate(aemnpp98a.getDataNotificacaoAutuacaoEdital(), "dd/MM/yyyy"));
                infracaoRenainf.setDataNotificacaoPenalidadeEdital(Utils.getDate(aemnpp98a.getDataNotificacaoPenalidadeEdital(), "dd/MM/yyyy"));
                infracaoRenainf.setDataOcorrencia(Utils.getDate(aemnpp98a.getDataOcorrencia(), "dd/MM/yyyy"));
                infracaoRenainf.setDataRegistroOcorrencia(Utils.getDate(aemnpp98a.getDataRegistroOcorrencia(), "dd/MM/yyyy"));
                infracaoRenainf.setDataVencimentoNotificacaoPenalidade(Utils.getDate(aemnpp98a.getDataVencimentoNotificacaoPenalidade(), "dd/MM/yyyy"));
                infracaoRenainf.setDescricaoCnhInfrator(aemnpp98a.getDescricaoCnhInfrator());
                infracaoRenainf.setDescricaoCnhInfratorPontuacao(aemnpp98a.getDescricaoCnhInfratorPontuacao());
                infracaoRenainf.setDescricaoIndicativoRestricaoRenajud(aemnpp98a.getDescricaoIndicativoRestricaoRenajud());
                infracaoRenainf.setDescricaoIndicativoRouboFurto(aemnpp98a.getDescricaoIndicativoRouboFurto());
                infracaoRenainf.setDescricaoMotivoDesvinculacao(aemnpp98a.getDescricaoMotivoDesvinculacao());
                infracaoRenainf.setDescricaoOcorrencia(aemnpp98a.getDescricaoOcorrencia());
                infracaoRenainf.setDescricaoOrigemPossuidor(aemnpp98a.getDescricaoOrigemPossuidor());
                infracaoRenainf.setDescricaoPenalidade(aemnpp98a.getDescricaoPenalidade());
                infracaoRenainf.setDescricaoRestricao1(aemnpp98a.getDescricaoRestricao1());
                infracaoRenainf.setDescricaoRestricao2(aemnpp98a.getDescricaoRestricao2());
                infracaoRenainf.setDescricaoRestricao3(aemnpp98a.getDescricaoRestricao3());
                infracaoRenainf.setDescricaoRestricao4(aemnpp98a.getDescricaoRestricao4());
                infracaoRenainf.setDescricaoRestricaoRenavam(aemnpp98a.getDescricaoRestricaoRenavam());
                infracaoRenainf.setDescricaoStatusAnalisePontuacao(aemnpp98a.getDescricaoStatusAnalisePontuacao());
                infracaoRenainf.setDescricaoTipoDocumentoProprietario(aemnpp98a.getDescricaoTipoDocumentoProprietario());
                infracaoRenainf.setIndicacaoRestricaoRenajud(aemnpp98a.getIndicacaoRestricaoRenajud());
                infracaoRenainf.setIndicativoRestricaoRenavam(aemnpp98a.getIndicativoRestricaoRenavam());
                infracaoRenainf.setIndicativoRouboFurto(aemnpp98a.getIndicativoRouboFurto());
                infracaoRenainf.setMensagemCondutor(aemnpp98a.getMensagemCondutor());
                infracaoRenainf.setMensagemRenainf(aemnpp98a.getMensagemRenainf());
                infracaoRenainf.setModeloCnhInfrator(aemnpp98a.getModeloCnhInfrator());
                infracaoRenainf.setModeloCnhInfratorPontuacao(aemnpp98a.getModeloCnhInfratorPontuacao());
                infracaoRenainf.setMotivoDesvinculacao(aemnpp98a.getMotivoDesvinculacao());
                infracaoRenainf.setMunicipioUf(aemnpp98a.getMunicipioUf());
                infracaoRenainf.setNomeCarroceria(aemnpp98a.getNomeCarroceria());
                infracaoRenainf.setNomeCategoria(aemnpp98a.getNomeCategoria());
                infracaoRenainf.setNomeCor(aemnpp98a.getNomeCor());
                infracaoRenainf.setNomeEspecie(aemnpp98a.getNomeEspecie());
                infracaoRenainf.setNomeInfrator(aemnpp98a.getNomeInfrator());
                infracaoRenainf.setNomeMarca(aemnpp98a.getNomeMarca());
                infracaoRenainf.setNomeProprietario(aemnpp98a.getNomeProprietario());
                infracaoRenainf.setNomeTipoVeiculo(aemnpp98a.getNomeTipoVeiculo());
                infracaoRenainf.setNotificacaoAutuacaoEdital(aemnpp98a.getNotificacaoAutuacaoEdital());
                infracaoRenainf.setNotificacaoPenalidadeEdital(aemnpp98a.getNotificacaoPenalidadeEdital());
                infracaoRenainf.setNumeroDocumentoProprietario(aemnpp98a.getNumeroDocumentoProprietario());
                infracaoRenainf.setNumeroNotificacaoPenalidade(aemnpp98a.getNumeroNotificacaoPenalidade());
                infracaoRenainf.setNumeroProcesso(aemnpp98a.getNumeroProcesso());
                infracaoRenainf.setNumeroRegistroCnhInfrator(aemnpp98a.getNumeroRegistroCnhInfrator());
                infracaoRenainf.setNumeroRegistroCnhInfratorPontuacao(aemnpp98a.getNumeroRegistroCnhInfratorPontuacao());
                infracaoRenainf.setOrgaoAutuador(aemnpp98a.getOrgaoAutuador());
                infracaoRenainf.setOrigemOcorrencia(aemnpp98a.getOrigemOcorrencia());

                lInfracaoRenainf.add(infracaoRenainf);
            }
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar dados infracao renainf", processoAdministrativo.getCpf());
        }

        return lInfracaoRenainf;
    }

    /**
     * @param processo
     * @return
     */
    public List<DadosPABPMSInfracaoLocalWrapper> gerarDadosInfracaoLocal(ProcessoAdministrativo processo, List<String> extratos, List<String> autuadores) throws AppException {

        List<DadosPABPMSInfracaoLocalWrapper> listaDados = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(extratos) && !DetranCollectionUtil.ehNuloOuVazio(autuadores)) {

            List<List<String>> listaExtratoParticionada     = DetranCollectionUtil.particionarLista(extratos, 4);
            List<List<String>> listaAutuadorParticionada    = DetranCollectionUtil.particionarLista(autuadores, 4);

            for (int bloco = 0; bloco < listaExtratoParticionada.size(); bloco++) {
                
                try {
                    
                    List<String> extratoBloco   = listaExtratoParticionada.get(bloco);
                    List<String> autuadorBloco  = listaAutuadorParticionada.get(bloco);

                    listaDados.addAll(executaAEMNPP97(processo, extratoBloco, autuadorBloco));

                } catch (AppException e) {
                    LOG.debug("Erro tratado.", e);
                    getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar dados infração local", processo.getCpf());
                }
            }
        }
            
        return listaDados;
    }

    /**
     * Verificar se já existe um arquivo Json gravado para o ProcessoAdministrativo. 
     * 
     * Se existir, definir data fim e ativo = 0.
     *
     * Gravar o arquivoJson com situacao = GERADO.
     *
     * @param em
     * @param processo
     * @param pabpmsWrapper
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public void gravarArquivoJson(EntityManager em, ProcessoAdministrativo processo, PABPMSWrapper pabpmsWrapper) throws DatabaseException, AppException {

        ProcessoAdministrativoJson paJsonExistente = 
                new ProcessoAdministrativoJsonRepositorio()
                        .getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo(em, processo.getId());

        if (null != paJsonExistente) {

            paJsonExistente.setDataFim(Calendar.getInstance().getTime());
            paJsonExistente.setAtivo(AtivoEnum.DESATIVADO);

            new ProcessoAdministrativoJsonRepositorio().update(em, paJsonExistente);
        }

        ProcessoAdministrativoJson paJson = new ProcessoAdministrativoJson();
        paJson.setSituacao(PASituacaoJsonEnum.GERADO);
        paJson.setJson(DetranStringUtil.getInstance().toJson(pabpmsWrapper));
        paJson.setDataInicio(Calendar.getInstance().getTime());
        paJson.setProcessoAdministrativo(processo);
        paJson.setAtivo(AtivoEnum.ATIVO);
                        
        new ProcessoAdministrativoJsonRepositorio().insert(em, paJson);
    }

    /**
     * @param em
     * @return
     */
    public List<PABPMSWrapper> getInstauradosJson(EntityManager em) {

        List<PABPMSWrapper> listaJson = new ArrayList();

        try {

            List<ProcessoAdministrativoJson> instaurados = 
                    new ProcessoAdministrativoJsonRepositorio()
                            .getPAsInstauradosAguardaFinalizaInstauracao(em);

            if (!DetranCollectionUtil.ehNuloOuVazio(instaurados)) {

                for (ProcessoAdministrativoJson instauradoJson : instaurados) {

                    try {

                        listaJson.add((PABPMSWrapper) DetranStringUtil.getInstance().fromJson(instauradoJson.getJson(), PABPMSWrapper.class));

                    } catch (Exception ex) {
                        LOG.debug("Sem tratamento.", ex);
                        getControleFalha().gravarFalha(ex, "Erro ao recuperar instaurados");
                    }
                }
            }

        } catch (Exception ex) {
            LOG.debug("Sem tratamento adequado para restrição.", ex);
            listaJson.add(gerarWrapperErro(ex.getMessage()));
        }

        return listaJson;
    }

    /**
     * 
     * @param processo
     * @param dadosCondutor
     * @return
     * @throws AppException 
     */
    public DadosPABPMSArtigoDespachoWrapper geraArtigoDespacho(EntityManager em,
                                                               ProcessoAdministrativo processo,
                                                               DadosPABPMSCondutorWrapper dadosCondutor,
                                                               ProcessoAdministrativoInfracao infracao) throws AppException {

        DadosPABPMSArtigoDespachoWrapper wrapper = new DadosPABPMSArtigoDespachoWrapper();

        try {

            wrapper.setCpfWork(processo.getCpf());
            wrapper.setNumeroPortaria(processo.getNumeroPortaria());
            wrapper.setProcesso(processo.getNumeroProcesso());
            wrapper.setTipoProcessoWork(processo.getTipo().name());
            wrapper.setArtigoIncisoWork(processo.getOrigemApoio().getArtigoInciso());
            wrapper.setDataCadastroWork(processo.getDataProcessamento());
            wrapper.setAnoPortaria(processo.getNumeroPortaria() == null ? null : processo.getNumeroPortaria().substring(0, 4));
            wrapper.setNomeWork(dadosCondutor.getNomeCondutor());
            wrapper.setNumeroCnhWork(processo.getCnh());
            wrapper.setNumeroRegistroWork(dadosCondutor.getNumeroRegistro());

            PAAmparoLegalWrapper amparo;

            List<RegraInstaurarEnum> cenarios = DetranCollectionUtil.
                    montaLista(RegraInstaurarEnum.C6,
                            RegraInstaurarEnum.C7,
                            RegraInstaurarEnum.C9,
                            RegraInstaurarEnum.C12);

            if (cenarios.contains(processo.getOrigemApoio().getRegra())) {
                amparo = new PAInfracaoRepositorio().
                        getAmparoLegalComArtigoIncisoParagrafoPorCodigoInfracao(em, infracao.getCodigoInfracao());
            } else {
                amparo = new PAInfracaoRepositorio().
                        getAmparoLegalComArtigoIncisoParagrafoPorId(
                                em, processo.getOrigemApoio().getAmparoLegal()
                        );
            }

            if (amparo != null) {
                wrapper.setArtigo(amparo.getArtigo());
                wrapper.setInciso(amparo.getInciso());
                wrapper.setParagrafo(amparo.getParagrafo());
            }

        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar artigo despacho", dadosCondutor.getCpf());
        }

        return wrapper;
    }
    
    /**
     * @param ex
     * @return
     */
    private PABPMSWrapper gerarWrapperErro(String ex) {

        PABPMSWrapper jsonWrapper = new PABPMSWrapper();

        jsonWrapper.setErro(new DadosPABPMSErroWrapper(ex));

        return jsonWrapper;
    }

    /**
     * 
     * @param infracoes
     * @return
     * @throws AppException 
     */
    public List<DadosPABPMSDetalhesAutuacaoInfracaoWrapper> geraDetalhesAutuacaoInfracaoWrapper(
        DadosPABPMSCondutorWrapper dadosCondutor, List<ProcessoAdministrativoInfracao> infracoes) throws AppException {
        
        List<DadosPABPMSDetalhesAutuacaoInfracaoWrapper> lDetalhesAutuacaoInfracao = new ArrayList();
        
        try {
        
            for (ProcessoAdministrativoInfracao infracao : infracoes) {

                /** Infração com Origem = 1 -> consulta BASE LOCAL. **/
                if(ORIGEM_INFRACAO_LOCAL.equals(infracao.getOrigemInfracao())) {
                
                    Integer situacaoPesquisa    = 0;
                    Integer qtdExtratos         = 0;

                    do {

                        AEMNPP92 aemnpp92
                            = new AEMNPP92BO()
                                .executarIntegracaoAEMNPP92(
                                    infracao.getAutoInfracao(), 
                                    situacaoPesquisa, 
                                    qtdExtratos
                                );

                        if (aemnpp92 == null || DetranCollectionUtil.ehNuloOuVazio(aemnpp92.getInformacoesExtratos())) {
                            DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
                        }

                        for (Object object : aemnpp92.getInformacoesExtratos()) {

                            AEMNPP92A aemnpp92a = (AEMNPP92A) object;

                            DadosPABPMSDetalhesAutuacaoInfracaoWrapper wrapper 
                                = new DadosPABPMSDetalhesAutuacaoInfracaoWrapper(
                                    infracao.getAutoInfracao(), 
                                    aemnpp92a.getCodigoInfracao(), 
                                    aemnpp92a.getOrgaoAutuador(), 
                                    aemnpp92a.getTipoInfracao(), 
                                    aemnpp92a.getResponsavelPontos(), 
                                    aemnpp92a.getPlaca(), 
                                    aemnpp92a.getMarcaModelo(), 
                                    aemnpp92a.getCodMunicipioVeiculo(), 
                                    aemnpp92a.getUfMunicipioVeiculo(), 
                                    aemnpp92a.getDescricaoInfracao(), 
                                    aemnpp92a.getValorInfracao(), 
                                    aemnpp92a.getCodigoTipoInfracao(), 
                                    aemnpp92a.getDescricaoTipoInfracao(), 
                                    aemnpp92a.getCompetencia(), 
                                    Utils.getDate(aemnpp92a.getDataInfracao().concat(" ").concat(aemnpp92a.getHoraInfracao()), "ddMMyyyy HHmm"),
                                    Utils.getDate(aemnpp92a.getDataLimiteDefesa(), "ddMMyyyy"),
                                    Utils.getDate(aemnpp92a.getDataEmissaoNotAut(), "ddMMyyyy"),
                                    Utils.getDate(aemnpp92a.getDataPublicacaoDiarioOficial(), "ddMMyyyy"),
                                    aemnpp92a.getLocalInfracao(), 
                                    aemnpp92a.getCodMunicipioInfracao(), 
                                    aemnpp92a.getNomeMunicipioInfracao(), 
                                    aemnpp92a.getVelocidadeConsiderada(), 
                                    aemnpp92a.getTipoAutuador(), 
                                    aemnpp92a.getDescricaoAutuador(), 
                                    aemnpp92a.getCodigoLocalEletronico(), 
                                    aemnpp92a.getNomeCondutor(), 
                                    aemnpp92a.getCondutorIdentificado(), 
                                    aemnpp92a.getCpfCondutor(), 
                                    aemnpp92a.getPguRegistroCondutor(), 
                                    aemnpp92a.getCnhCondutor() == null ? null : Long.parseLong(aemnpp92a.getCnhCondutor()),
                                    aemnpp92a.getSituacaoNotificacao(), 
                                    aemnpp92a.getCodigoRenainfInfracao(), 
                                    aemnpp92a.getGuiaPagamento(), 
                                    aemnpp92a.getGuiaPenalidade(), 
                                    aemnpp92a.getInformacaoExcessoPeso(), 
                                    aemnpp92a.getDescricaoStatusInfracao(),
                                    Utils.getDate(aemnpp92a.getDataCadastroInfracao(), "ddMMyyyy"),
                                    aemnpp92a.getInformacaoProcedentes(), 
                                    aemnpp92a.getNomeProprietarioCondutor(), 
                                    aemnpp92a.getPguProprietarioCondutor(), 
                                    aemnpp92a.getNumRegistroProprietarioCondutor(), 
                                    aemnpp92a.getCpfCnpjProprietarioCondutor(), 
                                    aemnpp92a.getNumeroObjetoNotificacao(), 
                                    aemnpp92a.getNumeroObjetoPenalizacao(), 
                                    aemnpp92a.getSituacaoAtualNotificacaoAR(), 
                                    aemnpp92a.getNomeRecebedorNotificacao(), 
                                    aemnpp92a.getDocumentoRecebedor(), 
                                    Utils.getDate(aemnpp92a.getDataRecebimentoNotificacao(), "ddMMyyyy"), 
                                    aemnpp92a.getNomeProprietario(),
                                    aemnpp92a.getEndereco(), 
                                    aemnpp92a.getNumeroEndereco(), 
                                    aemnpp92a.getComplemento(), 
                                    aemnpp92a.getBairro(), 
                                    aemnpp92a.getNomeMunicipio(), 
                                    aemnpp92a.getUfMunicipio(), 
                                    aemnpp92a.getCep(), 
                                    aemnpp92a.getNumeroDocProprietario(), 
                                    aemnpp92a.getSituacaoRecurso(), 
                                    aemnpp92a.getResultadoRecurso(), 
                                    aemnpp92a.getNomeRequerente(), 
                                    Utils.getDate(aemnpp92a.getDataProtocoloRecurso(), "ddMMyyyy"),
                                    aemnpp92a.getNumeroProcessoRecurso(), 
                                    aemnpp92a.getAgenteAutuador(), 
                                    aemnpp92a.getUsuarioCadastro(),
                                    Utils.getDate(aemnpp92a.getDataCadastramento().concat(" ").concat(aemnpp92a.getHoraCadastramento()), "ddMMyyyy HHmm"),
                                    aemnpp92a.getCodigoTerminalRede(), 
                                    Utils.getDate(aemnpp92a.getDataRelatorioDiarioOficial(), "ddMMyyyy"),
                                    aemnpp92a.getObservacaoCancelamento()
                                );

                            lDetalhesAutuacaoInfracao.add(wrapper);
                        }

                        situacaoPesquisa    = Integer.valueOf(aemnpp92.getSituacaoPesquisa().trim());
                        qtdExtratos         = Integer.valueOf(aemnpp92.getQtdInformacoesRetornadas().trim());

                    } while (situacaoPesquisa == 1);
                }
            }
        
        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar detalhes autuacao infracao (AEMNPP92)", dadosCondutor.getCpf());
        }
        
        return lDetalhesAutuacaoInfracao;
    }

    /**
     * 
     * @param processo
     * @param extratos
     * @param autuadores
     * @return
     * @throws AppException 
     */
    private List<DadosPABPMSInfracaoLocalWrapper> executaAEMNPP97(ProcessoAdministrativo processo, List<String> extratos, List<String> autuadores) throws AppException {

        List<DadosPABPMSInfracaoLocalWrapper> listaDados = new ArrayList();

        AEMNPP97 aemnpp97 = new AEMNPP97BO().executarIntegracao(processo.getCpf(), extratos, autuadores);

        if (aemnpp97 != null && !DetranCollectionUtil.ehNuloOuVazio(aemnpp97.getInformacoesExtratos())) {

            for (Object object : aemnpp97.getInformacoesExtratos()) {

                AEMNPP97A aemnpp97a = (AEMNPP97A) object;

                listaDados
                        .add(
                                new DadosPABPMSInfracaoLocalWrapper(
                                        aemnpp97a.getNumeroAuto(),
                                        aemnpp97a.getResponsavelInfracao(),
                                        aemnpp97a.getPlacaVeiculo(),
                                        Integer.parseInt(aemnpp97a.getCodigoMunicipioInfracao()),
                                        aemnpp97a.getCodigoInfracao(),
                                        aemnpp97a.getCompetenciaInfracao(),
                                        aemnpp97a.getTipoInfracao(),
                                        aemnpp97a.getDescricaoTipoInfracao(),
                                        Utils.getDate(aemnpp97a.getDataInfracao().concat(" ").concat(aemnpp97a.getHoraInfracao()), "dd/MM/yyyy HH:mm"),
                                        Utils.getDate(aemnpp97a.getDataNotificacao(), "dd/MM/yyyy"),
                                        Utils.getDate(aemnpp97a.getDataVencimentoInfracao(), "dd/MM/yyyy"),
                                        aemnpp97a.getSituacaoNotificacao(),
                                        aemnpp97a.getInformacaoBaixa(),
                                        aemnpp97a.getGuiaBaixa(),
                                        aemnpp97a.getValorPagamento(),
                                        Utils.getDate(aemnpp97a.getDataPagamento(), "dd/MM/yyyy"),
                                        Utils.getDate(aemnpp97a.getDataCriacaoRelatorioPublicacaoPenalidade(), "dd/MM/yyyy"),
                                        Utils.getDate(aemnpp97a.getDataPublicacaoPenalidade(), "dd/MM/yyyy"),
                                        aemnpp97a.getLocalInfracao(),
                                        Integer.parseInt(aemnpp97a.getCodigoMunicipioInfracao()),
                                        aemnpp97a.getNomeMunicipioInfracao(),
                                        Integer.parseInt(aemnpp97a.getVelocidadeAferida()),
                                        Integer.parseInt(aemnpp97a.getTipoAutuador()),
                                        aemnpp97a.getDescricaoTipoAutuador(),
                                        aemnpp97a.getNomeCondutor(),
                                        aemnpp97a.getInfratorNotificacao(),
                                        aemnpp97.getCpf(),
                                        aemnpp97a.getPguCondutor(),
                                        aemnpp97a.getCnhCondutor() == null ? null : Long.parseLong(aemnpp97a.getCnhCondutor()),
                                        aemnpp97a.getMarcaModeloVeiculo(),
                                        aemnpp97a.getMensagemParcelamento()
                                )
                        );
            }
        }

        return listaDados;
    }

    /**
     * 
     * @param pabpmsWrapper
     * @param processo
     * @param extratos
     * @param autuadores
     * @throws AppException 
     */
    public void executaAEMNPP98PorBlocos(
        PABPMSWrapper pabpmsWrapper, ProcessoAdministrativo processo, List<String> extratos, List<String> autuadores) throws AppException {
        
        
        if (!DetranCollectionUtil.ehNuloOuVazio(extratos) && !DetranCollectionUtil.ehNuloOuVazio(autuadores)) {

            List<List<String>> listaExtratoParticionada     = DetranCollectionUtil.particionarLista(extratos, 4);
            List<List<String>> listaAutuadorParticionada    = DetranCollectionUtil.particionarLista(autuadores, 4);

            List<DadosPABPMSRenavamWrapper> dadosRenavam            = new ArrayList();
            List<DadosPABPMSInfracaoRenainfWrapper> dadosRenainf    = new ArrayList();
            
            for (int bloco = 0; bloco < listaExtratoParticionada.size(); bloco++) {
                
                try {
                    
                    List<String> extratoBloco   = listaExtratoParticionada.get(bloco);
                    List<String> autuadorBloco  = listaAutuadorParticionada.get(bloco);
                    
                    AEMNPP98 AEMNPP98
                        = new AEMNPP98BO().executarIntegracaoAEMNPP98(processo.getCpf(), extratoBloco, autuadorBloco);

                    if (AEMNPP98 == null || DetranCollectionUtil.ehNuloOuVazio(AEMNPP98.getInformacoesExtratos())) {
                        DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
                    }
                    
                    dadosRenavam.addAll(geraDadosRenavam(processo, AEMNPP98));
                    dadosRenainf.addAll(geraDadosInfracaoRenainf(processo, AEMNPP98));

                } catch (AppException e) {
                    LOG.debug("Erro tratado.", e);
                    getControleFalha().gravarFalhaCondutor(e, "Erro ao executar AEMNPP98", processo.getCpf());
                }
            }
            
            /** Dados Renavam. **/
            pabpmsWrapper.setDadosRenavam(dadosRenavam);
            
            /** Dados Infracao Renainf. **/
            pabpmsWrapper.setDadosInfracaoRenainf(dadosRenainf);
        }
    }

    /**
     * @param em
     * @param processo
     * @return 
     */
    public DadosPABPMSJuridicoWrapper geraDadosJuridicos(EntityManager em, ProcessoAdministrativo processo) {
        
        DadosPABPMSJuridicoWrapper wrapper = new DadosPABPMSJuridicoWrapper();

        try {
            
            DadoProcessoJudicial dadoProcessoJudicial = 
                    new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processo.getId());
            
            if (null != dadoProcessoJudicial) {
                
                if (DetranStringUtil.ehBrancoOuNulo(dadoProcessoJudicial.getValorAutos())
                        || null == dadoProcessoJudicial.getParteProcessoJuridico()
                        || null == dadoProcessoJudicial.getOrgaoJudicial()
                        || null == dadoProcessoJudicial.getTipoMedida()
                        || null == dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh()
                        || null == dadoProcessoJudicial.getRequisitoCursoBloqueio()
                        || null == dadoProcessoJudicial.getOrgaoBca()
                        || null == dadoProcessoJudicial.getUsuarioCadastro()
                        || null == dadoProcessoJudicial.getDataCadastro()) {

                    DetranWebUtils.applicationMessageException("Dados obrigatórios não encontrados.");

                }

                OrgaoJudicial orgaoJudicial = (OrgaoJudicial) getApoioService().getOrgaoJudicialPorId(dadoProcessoJudicial.getOrgaoJudicial());

                if (null == orgaoJudicial
                        || DetranStringUtil.ehBrancoOuNulo(orgaoJudicial.getDescricao())
                        || null == orgaoJudicial.getTribunal()
                        || DetranStringUtil.ehBrancoOuNulo(orgaoJudicial.getTribunal().getDescricao())) {

                    DetranWebUtils.applicationMessageException("Dados obrigatórios não encontrados.");
                }

                OrgaoBca orgaoBca = (OrgaoBca) getApoioService().getOrgaoBcaPorID(dadoProcessoJudicial.getOrgaoBca());

                if (null == orgaoBca
                        || DetranStringUtil.ehBranco(orgaoBca.getDescricao())) {
                    
                    DetranWebUtils.applicationMessageException("Dados obrigatórios não encontrados.");
                    
                }
                
                Usuario usuario = (Usuario) getApoioService().getUsuario(dadoProcessoJudicial.getUsuarioCadastro());
                
                if (null == usuario
                        || DetranStringUtil.ehBrancoOuNulo(usuario.getLogin())) {
                    
                    DetranWebUtils.applicationMessageException("Dados obrigatórios não encontrados.");
                    
                }
                
                //OBRIGATÓRIOS
                wrapper.setValorAutos(dadoProcessoJudicial.getValorAutos());
                wrapper.setParte(dadoProcessoJudicial.getParteProcessoJuridico().name());
                wrapper.setOrgaoJudicial(orgaoJudicial.getDescricao());
                wrapper.setTribunal(orgaoJudicial.getTribunal().getDescricao());
                wrapper.setTipoMedida(dadoProcessoJudicial.getTipoMedida().name());
                wrapper.setIdentificacaoRecolhimentoCnh(dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh().name());
                wrapper.setRequisitoCursoBloqueio(dadoProcessoJudicial.getRequisitoCursoBloqueio().name());
                wrapper.setOrgaoBca(orgaoBca.getDescricao());
                wrapper.setCodigoOrgaoBca(orgaoBca.getCodigo().toString());
                wrapper.setUsuario(usuario.getLogin()); 
                wrapper.setDataCadastro(dadoProcessoJudicial.getDataCadastro().toString());
                wrapper.setSegredoJustica(dadoProcessoJudicial.getSegredoJustica().name());
                
                // NÃO OBRIGATÓRIOS
                wrapper.setFormularioRenach(dadoProcessoJudicial.getFormularioRenach());
                
                if (null != dadoProcessoJudicial.getIdentificacaoDelito())
                    wrapper.setIndicativoDelito(dadoProcessoJudicial.getIdentificacaoDelito().name());
                
                wrapper.setObservacao(dadoProcessoJudicial.getObservacao());
                
                List<DadoProcessoJuridicoDelito> listaDadoProcessoJuridicoDelito = 
                        new DadoProcessoJudicialDelitoRepositorio()
                                .getDadoProcessoJudicialDelitoPorDadoProcessoJudicial(
                                        em, 
                                        dadoProcessoJudicial.getId());
                if (!DetranCollectionUtil.ehNuloOuVazio(listaDadoProcessoJuridicoDelito)) {
                    List<DadosPABPMSJuridicoArtigoDelitoWrapper> listaDelitos = new ArrayList();
                    for (DadoProcessoJuridicoDelito dadoProcessoJuridicoDelito : listaDadoProcessoJuridicoDelito) {
                        AmparoLegal amparoLegal = 
                                (AmparoLegal) getApoioService().getAmparoLegalPorId(dadoProcessoJuridicoDelito.getAmparoLegal());
                        DadosPABPMSJuridicoArtigoDelitoWrapper delito = new DadosPABPMSJuridicoArtigoDelitoWrapper();
                        if (null != amparoLegal)
                            delito.setArtigoDelito(amparoLegal.getArtigo() + " " 
                                                    + amparoLegal.getParagrafo() + " " 
                                                    + amparoLegal.getInciso() + " "
                                                    + amparoLegal.getReferencia());
                    }
                    wrapper.setDelitos(listaDelitos);
                }
            }

        } catch (AppException e) {
            LOG.debug("Erro tratado.", e);
            getControleFalha().gravarFalhaCondutor(e, "Erro ao gerar dados jurídicos", processo.getCpf());
        }

        return wrapper;
    }
}