package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.RepresentanteLegal;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PADocumentoPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PADocumentoPessoaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacoesEntregaCnhWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

public class EntregaCnhBO {
    
    private IApoioService apoioService;
    private IPAControleFalhaService falhaService;
    private IControleCnhService controleCnhService;
    
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
     * @return
     */
    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService =  (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }
    
    public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }

    /**
     * @param em
     * @return
     * @throws AppException 
     */
    public List getInformacoesEntregaCnh(EntityManager em) throws AppException {

        List lEntrega = new ArrayList();
        
        lEntrega.addAll(getInformacoesEntregaCnh(em, new MovimentoCnhRepositorio().getListEntregaCnh(em)));
        
        lEntrega.addAll(getInformacoesEntregaCnhPJUCartorioECandidatoCidadao(em));
        
        lEntrega.addAll(getInformacoesDevolucaoCnhPenaCumpridaAndamento140(em));
        
        return lEntrega;
        
    }
    
    /**
     * 
     * @param em
     * @param listaMovimentoCnh
     * @return
     * @throws AppException 
     */
    public List getInformacoesEntregaCnh(EntityManager em, List<MovimentoCnh> listaMovimentoCnh) throws AppException {
        
        List<InformacoesEntregaCnhWrapper> lista = new ArrayList<>();
        
        if (!DetranCollectionUtil.ehNuloOuVazio(listaMovimentoCnh)) {

            for (MovimentoCnh movimentoCnh : listaMovimentoCnh) {
                
                try {
                    
                    InformacoesEntregaCnhWrapper item = montarInformacoesEntregaCnhWrapper(em, movimentoCnh);
                    
                    if (item != null) {
                        lista.add(item);
                    }
                    
                } catch (AppException e) {
                    
                    Logger.getLogger(EntregaCnhBO.class).debug("Erro: ", e);

                    getFalhaService().
                        gravarFalhaProcessoAdministrativo(
                            e, 
                            "MONTAR_INFORMACOES_ENTREGA", 
                            movimentoCnh.getProtocolo().getNumeroProcesso().getCpf(), 
                            movimentoCnh.getProtocolo().getNumeroProcesso().getNumeroProcesso()
                        );
                }
            }
        }
        
        return lista;
    }

    /**
     * @param em
     * @param movimentoCnh
     * @return
     * @throws AppException 
     */
    public InformacoesEntregaCnhWrapper montarInformacoesEntregaCnhWrapper(
        EntityManager em, MovimentoCnh movimentoCnh) throws AppException {
        
        InformacoesEntregaCnhWrapper wrapper = null;
        
        CnhSituacaoEntrega cnhSituacaoEntrega = 
            (CnhSituacaoEntrega) getApoioService()
                    .getSituacaoEntregaCnh(movimentoCnh.getCnhControle(), movimentoCnh.getAcao());

        if (null == cnhSituacaoEntrega) {
            DetranWebUtils.applicationMessageException("Situação entrega da CNH não encontrada.");
        }

        wrapper = new InformacoesEntregaCnhWrapper();

        wrapper.setNumeroProcesso(movimentoCnh.getProtocolo().getNumeroProcesso().getNumeroProcesso());
        wrapper.setNumeroProtocolo(movimentoCnh.getProtocolo().getNumeroProtocolo());
        wrapper.setAcao(movimentoCnh.getAcao().name());
        wrapper.setCnhRecolhida(cnhSituacaoEntrega.getCnhControle().getNumeroCnh());
        
        PADocumentoPessoaWrapper documentoWrapper = new PADocumentoPessoaRepositorio().
                getCpfDocumentoPessoa(em, cnhSituacaoEntrega.getUsuario().getPessoa().getId());
        if (documentoWrapper != null) {
            wrapper.setCpfUsuario(documentoWrapper.getNumeroDocumento());
            wrapper.setNomeUsuario(documentoWrapper.getNomePrincipalPessoa());
        }

        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                getTemplateProtocoloPorID(movimentoCnh.getProtocolo().getTemplateProtocolo());
        
        wrapper.setObservacao(template.getObservacao());
        
        BloqueioBCA bloqueio = 
                new BloqueioBCARepositorio()
                        .getBloqueioBCAporPA(em, movimentoCnh.getProtocolo().getNumeroProcesso().getId());

        if (AcaoEntregaCnhEnum.ENTREGA.equals(cnhSituacaoEntrega.getAcao())) {

            PAComplemento complemento
                    = new PAComplementoRepositorio()
                            .getPAComplementoPorParametroEAtivo(
                                    em,
                                    movimentoCnh.getProtocolo().getNumeroProcesso(),
                                    PAParametroEnum.TEMPO_PENALIDADE);

            PAPenalidadeProcesso penalidade
                    = new PAPenalidadeProcessoRepositorio()
                            .getPenalidadePorPA(em, movimentoCnh.getProtocolo().getNumeroProcesso().getId());

            wrapper.setDataEntrega(cnhSituacaoEntrega.getDataEntrega());

            wrapper.setPrazoPenalidade(complemento == null ? "SEM_DEFINICAO" : complemento.getValor());
            wrapper.setDataInicioPenalidade(penalidade == null ? null : penalidade.getDataInicioPenalidade());
            wrapper.setDataFimPenalidade(penalidade == null ? null : penalidade.getDataFimPenalidade());

            wrapper.setFormaEntrega(cnhSituacaoEntrega.getModoEntrega() == null ? null : cnhSituacaoEntrega.getModoEntrega().name());
            wrapper.setNumeroProcessoOriginal(movimentoCnh.getProcessoOriginal() != null ? movimentoCnh.getProcessoOriginal().getNumeroProcesso() : null);

        } else {

            if (bloqueio != null) {
                wrapper.setMotivoDesbloqueioBCA(bloqueio.getMotivoDesbloqueio().name());
            }

            wrapper.setDataDevolucao(cnhSituacaoEntrega.getDataEntrega());
        }

        RepresentanteLegal representanteLegal
                = (RepresentanteLegal) getControleCnhService().getRepresentanteLegalPorTemplateProtocolo(template.getId());

        if (representanteLegal != null) {
            wrapper.setCpfRepresentanteLegal(representanteLegal.getCpf());
            wrapper.setNomeRepresentanteLegal(representanteLegal.getNome());
        }

        return wrapper;
    }

    private List getInformacoesEntregaCnhPJUCartorioECandidatoCidadao(EntityManager em) throws AppException {
        
        List<InformacoesEntregaCnhWrapper> lEntrega = new ArrayList<>();
        
        List<ProcessoAdministrativo> processosCartorioParaEntrega = 
                    new ProcessoAdministrativoRepositorio().
                                getListaProcessoAdministrativoPorAndamento(em, 
                                                                           DetranCollectionUtil.montaLista(
                                                                           PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_ENTREGA_CNH_CARTORIO
                                                                           ));
        
        List<ProcessoAdministrativo> processosCartorioParaDevolucao = 
                    new ProcessoAdministrativoRepositorio().
                                getListaProcessoAdministrativoPorAndamento(em, 
                                                                           DetranCollectionUtil.montaLista(
                                                                           PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_DESBLOQUEIO_CARTORIO
                                                                           ));
        
        List<ProcessoAdministrativo> processosCidadaoCandidatoParaDevolucao = 
                    new ProcessoAdministrativoRepositorio().
                        getListaProcessoAdministrativoPorAndamento(em, 
                                                                   DetranCollectionUtil.montaLista(
                                                                   PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_DESBLOQUEIO_CANDIDATO_CIDADAO_PJU
                                                                   ));
        
        lEntrega.addAll(montarInformacoesEntregaDevolucaoCnhPJUCartorio(em, 
                                                                        processosCartorioParaEntrega, 
                                                                        AcaoEntregaCnhEnum.ENTREGA.name()));
        
        lEntrega.addAll(montarInformacoesEntregaDevolucaoCnhPJUCartorio(em, 
                                                                        processosCartorioParaDevolucao, 
                                                                        AcaoEntregaCnhEnum.DEVOLUCAO.name()));
        
        lEntrega.addAll(montarInformacoesEntregaDevolucaoCnhPJU(em, 
                                                                processosCidadaoCandidatoParaDevolucao, 
                                                                AcaoEntregaCnhEnum.DEVOLUCAO.name(),
                                                                "CIDADAO OU CANDIDATO SEM CNH."));
        return lEntrega;
    }

    
    private List<InformacoesEntregaCnhWrapper> montarInformacoesEntregaDevolucaoCnhPJUCartorio(EntityManager em, 
                                                                 List<ProcessoAdministrativo> processosCartorio, String acao) throws AppException {
        return montarInformacoesEntregaDevolucaoCnhPJU(em, processosCartorio, acao, "CNH ENTREGUE NO CARTÓRIO");
    }
    
    private List<InformacoesEntregaCnhWrapper> montarInformacoesEntregaDevolucaoCnhPJU(EntityManager em, 
                                                                 List<ProcessoAdministrativo> processosCartorio, String acao, String obs) throws AppException {
        
        List<InformacoesEntregaCnhWrapper> lEntrega = new ArrayList<>();
        
        for (ProcessoAdministrativo processoAdministrativo : processosCartorio) {
            
            InformacoesEntregaCnhWrapper wrapper = new InformacoesEntregaCnhWrapper();
            wrapper.setAcao(acao);
            wrapper.setObservacao(obs);
            wrapper.setCnhRecolhida(processoAdministrativo.getCnh());
            wrapper.setNumeroProcesso(processoAdministrativo.getNumeroProcesso());
            
            BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBCAporPA(em, processoAdministrativo.getId());
            if(bloqueio != null){
                wrapper.setDataBloqueio(bloqueio.getDataInicio());
                wrapper.setMotivoDesbloqueioBCA(bloqueio.getMotivoDesbloqueio() == null? null : bloqueio.getMotivoDesbloqueio().name());
            }
            
            PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processoAdministrativo.getId());
            if(penalidade != null){
                wrapper.setPrazoPenalidade(penalidade.getValor().toString());
                wrapper.setDataInicioPenalidade(penalidade.getDataInicioPenalidade());
                wrapper.setDataFimPenalidade(penalidade.getDataFimPenalidade());
            }
            
            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());
            if(pju != null){
                wrapper.setDataEntrega(pju.getDataEntrega());
                Usuario usuario = (Usuario) getApoioService().getUsuario(pju.getProcessoJudicial().getUsuarioCadastro());
         
                PADocumentoPessoaWrapper documentoWrapper = new PADocumentoPessoaRepositorio().
                        getCpfDocumentoPessoa(em, usuario.getPessoa().getId());
                if (documentoWrapper != null) {
                    wrapper.setCpfUsuario(documentoWrapper.getNumeroDocumento());
                    wrapper.setNomeUsuario(documentoWrapper.getNomePrincipalPessoa());
                }
            }
            
            lEntrega.add(wrapper);
        }
        return lEntrega;
    }
    
    public List getInformacoesDevolucaoCnhPenaCumpridaAndamento140(EntityManager em) throws DatabaseException {
        List<ProcessoAdministrativo> processos = new ProcessoAdministrativoRepositorio().getProcessosAndamento140ComPenaCumpridaParaWSEntregaCNH(em);
        
        List<InformacoesEntregaCnhWrapper> lDevolucao = new ArrayList<>();
        
        for(ProcessoAdministrativo processo : processos){
            InformacoesEntregaCnhWrapper wrapper = new InformacoesEntregaCnhWrapper();
            
            wrapper.setNumeroProcesso(processo.getNumeroProcesso());
            wrapper.setAcao(AcaoEntregaCnhEnum.DEVOLUCAO.name());
            
            lDevolucao.add(wrapper);
        }
        
        return lDevolucao;
    }
}