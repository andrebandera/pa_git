package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.Notificacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCodigoBarraHelper;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ArquivoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProtocoloRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.PROTOCOLO_PA;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.util.SequencialRESTfulService;
import br.gov.ms.detran.processo.administrativo.wrapper.MovimentoCnhWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;

public class ProtocoloBO extends FabricaJSPASequencial {
    
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

    public Protocolo gravar(EntityManager em, 
                            PAFluxoFase pAFluxoFase, 
                            TipoNotificacaoEnum tipo, 
                            ProcessoAdministrativo pa,
                            TemplateProtocolo template) throws AppException {

        Protocolo protocolo = new Protocolo();

        protocolo.setNumeroProcesso(pa);
        protocolo.setFluxoFase(pAFluxoFase);
        protocolo.setEditalCorpo(getIdEdital(tipo));
        protocolo.setNumeroProtocolo(recuperarNumeroProtocolo());
        protocolo.setByteCodigoBarra(DetranCodigoBarraHelper.codigoBarra2de5(DetranStringUtil.preencherEspaco(protocolo.getNumeroProtocolo(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO)));
        protocolo.setTemplateProtocolo(template.getId());
        protocolo.setDataProtocolo(template.getDataProtocolo());
        
        return new ProtocoloRepositorio().insert(em, protocolo);
    }

    /**
     * @param em
     * @param wrapper
     * @param urlBaseBirt
     * @return 
     * @throws AppException 
     */
    public ArquivoPA gravarArquivoProtocoloControleCnh(
        EntityManager em, MovimentoCnhWrapper wrapper, String urlBaseBirt) throws AppException {
    
        //Parametriza nome do arquivo e usuário logado para BIRT recebendo o Byte Arquivo.
        Map<String, String> parametros = new HashMap<>();
        parametros.put("v_id_movimento", wrapper.getEntidade().getId().toString());
        
        wrapper.setByteArquivo(
            DetranHTTPUtil
                .download(
                    urlBaseBirt 
                        + DetranReportsUtil.getReportParamsBirtUrl(
                            "protocolo_controle_cnh",
                            FormatoRelatorioEnum.PDF.getRptFormat(),
                            parametros,
                            "relatorios/processoadministrativo/entregacnh/"
                        )
                )
        );
        
        if (wrapper.getByteArquivo() == null) {
            DetranWebUtils.applicationMessageException("severity.error.application.summary.databaseexception");
        }
        
        if (wrapper.getEntidade() == null || wrapper.getByteArquivo() == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
        
        Protocolo protocolo = wrapper.getEntidade().getProtocolo();
        
        if (protocolo == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
            
        ArquivoPA arquivoPA = new ArquivoPA();

        TipoArquivo tipoArquivo = 
            (TipoArquivo) getApoioService()
                .getTipoArquivoPorCodigoEModulo(PROTOCOLO_PA, ModuloTipoArquivoEnum.PA);

        arquivoPA.setTipoArquivo(tipoArquivo.getId());
        arquivoPA.setDescricao("PROTOCOLO ENTREGA/DEVOLUCAO CNH");
        arquivoPA.setExtensao(TipoExtensaoArquivoEnum.PDF);
        arquivoPA.setTabela("TB_TDM_PAD_PROTOCOLO");
        arquivoPA.setIdTabela(protocolo.getId());
        arquivoPA.setAtivo(AtivoEnum.ATIVO);
        
        arquivoPA.setByteArquivo(wrapper.getByteArquivo());

        new ArquivoPARepositorio().insertOrUpdate(em, arquivoPA);
        
        protocolo.setArquivoPa(arquivoPA);
        
        new ProtocoloRepositorio().update(em, protocolo);
        
        return arquivoPA;
    }
    
    public ArquivoPA gravarArquivoProtocoloDesistenciaInstPen(
        EntityManager em, Protocolo protocolo, String urlBaseBirt) throws AppException {
    
        //Parametriza nome do arquivo e usuário logado para BIRT recebendo o Byte Arquivo.
        Map<String, String> parametros = new HashMap<>();
        parametros.put("v_id_pa", protocolo.getNumeroProcesso().getId().toString());
        
        byte[] byteArquivo = 
            DetranHTTPUtil
                .download(
                    urlBaseBirt 
                        + DetranReportsUtil.getReportParamsBirtUrl(
                            "protocolo_desistencia_inst_pen",
                            FormatoRelatorioEnum.PDF.getRptFormat(),
                            parametros,
                            "relatorios/processoadministrativo/protocolodesistenciainstpen/"
                        )
        );
        
        if (byteArquivo == null) {
            DetranWebUtils.applicationMessageException("severity.error.application.summary.databaseexception");
        }
        
        
        
        if (protocolo == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
            
        ArquivoPA arquivoPA = new ArquivoPA();

        TipoArquivo tipoArquivo = 
            (TipoArquivo) getApoioService()
                .getTipoArquivoPorCodigoEModulo(PROTOCOLO_PA, ModuloTipoArquivoEnum.PA);

        arquivoPA.setTipoArquivo(tipoArquivo.getId());
        arquivoPA.setDescricao("PROTOCOLO DESISTENCIA RECURSO INSTAURACAO PENALIZACAO");
        arquivoPA.setExtensao(TipoExtensaoArquivoEnum.PDF);
        arquivoPA.setTabela("TB_TDM_PAD_PROTOCOLO");
        arquivoPA.setIdTabela(protocolo.getId());
        arquivoPA.setAtivo(AtivoEnum.ATIVO);
        
        arquivoPA.setByteArquivo(byteArquivo);

        new ArquivoPARepositorio().insertOrUpdate(em, arquivoPA);
        
        protocolo.setArquivoPa(arquivoPA);
        
        new ProtocoloRepositorio().update(em, protocolo);
        
        return arquivoPA;
    }

    public String recuperarNumeroProtocolo() throws AppException {
        return new SequencialRESTfulService().getNumeroProtocolo();
    }

    /**
     * @param tipo
     * @return
     * @throws AppException 
     */
    private Long getIdEdital(TipoNotificacaoEnum tipo) throws AppException {
        Notificacao corpo =  (Notificacao) getApoioService().getEditalCorpoPorTipo(tipo);
        return corpo == null ? null : corpo.getId();
    }
    
    /**
     * @param em
     * @param wrapper
     * @throws AppException 
     */
    public void incluiArquivoProtocoloRecurso(EntityManager em, RecursoWrapper wrapper) throws AppException {
        
        if (wrapper.getByteArquivo() == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
        
        RecursoMovimento recursoMovimento = 
            new RecursoMovimentoBO().getRecursoMovimentoPorRecursoETipoProtocolo(em, wrapper.getEntidade(), wrapper.getTipoProtocolo());
        
        if (recursoMovimento == null && wrapper.getProtocolo() == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
        
        Protocolo protocolo = recursoMovimento != null ? recursoMovimento.getProtocolo() : wrapper.getProtocolo();
        
        if (protocolo == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
        }
        
        ArquivoPA arquivoPa = protocolo.getArquivoPa();
        
        if(protocolo.getArquivoPa() == null) {
            
            arquivoPa = new ArquivoPA();
            
            TipoArquivo tipoArquivo = 
                (TipoArquivo) getApoioService().getTipoArquivoPorCodigoEModulo(PROTOCOLO_PA, ModuloTipoArquivoEnum.PA);

            arquivoPa.setTipoArquivo(tipoArquivo.getId());
            
            arquivoPa
                .setDescricao(
                    TipoSituacaoProtocoloEnum.APRESENTACAO.equals(wrapper.getTipoProtocolo()) ? 
                        "PROTOCOLO APRESENTACAO RECURSO" 
                            : "PROTOCOLO CANCELAMENTO RECURSO");
            
            arquivoPa.setExtensao(TipoExtensaoArquivoEnum.PDF);
            arquivoPa.setTabela("TB_TDM_PAD_PROTOCOLO");
            arquivoPa.setIdTabela(protocolo.getId());
            arquivoPa.setAtivo(AtivoEnum.ATIVO);
        }
        
        arquivoPa.setByteArquivo(wrapper.getByteArquivo());
        
        protocolo.setArquivoPa(new ArquivoPARepositorio().insertOrUpdate(em, arquivoPa));
        
        new AbstractJpaDAORepository().update(em, protocolo);
    }
    
    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public IBaseEntity getProtocoloRecurso(EntityManager em, RecursoWrapper wrapper) throws AppException {
        
        RecursoMovimento recursoMovimento = 
            new RecursoMovimentoBO()
                .getRecursoMovimentoPorRecursoETipoProtocolo(
                    em, 
                    wrapper.getEntidade(),
                    wrapper.getTipoProtocolo()
                );
        
        if (recursoMovimento == null || recursoMovimento.getProtocolo() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o protocolo.");
        }
        
        return recursoMovimento.getProtocolo().getArquivoPa();
    }
    
    /**
     * 
     * @param em
     * @param movimentoCnhDeEntregaCNH
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public Protocolo gravaProtocoloParaDesentranhamento(
        EntityManager em, MovimentoCnh movimentoCnhDeEntregaCNH, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        Protocolo protocoloDesentranhamento = new Protocolo();

        protocoloDesentranhamento.setEditalCorpo(movimentoCnhDeEntregaCNH.getProtocolo().getEditalCorpo());
        protocoloDesentranhamento.setNumeroProtocolo(recuperarNumeroProtocolo());
        protocoloDesentranhamento.setNumeroProcesso(processoAdministrativo);
        
        protocoloDesentranhamento
            .setByteCodigoBarra(
                DetranCodigoBarraHelper
                    .codigoBarra2de5(
                        DetranStringUtil
                            .preencherEspaco(
                                protocoloDesentranhamento.getNumeroProtocolo(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO)
                    )
            );
        
        protocoloDesentranhamento.setFluxoFase(new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, processoAdministrativo));
        protocoloDesentranhamento.setAtivo(AtivoEnum.ATIVO);
        protocoloDesentranhamento.setTemplateProtocolo(movimentoCnhDeEntregaCNH.getProtocolo().getTemplateProtocolo());
        protocoloDesentranhamento.setDataProtocolo(Calendar.getInstance().getTime());
        
        return new ProtocoloRepositorio().insert(em, protocoloDesentranhamento);
    }
    
    /**
     * 
     * @param em
     * @param protocolo
     * @throws AppException 
     */
    public void ajustaProtocoloRecursoGeraCodigoBarra(EntityManager em, Protocolo protocolo) throws AppException {
        
        if(protocolo == null || protocolo.getId() == null) {
            throw new AppException("Protocolo inválido.");
        }
        
        Protocolo protocoloAtualizar = new ProtocoloRepositorio().find(em, Protocolo.class, protocolo.getId());
        
        if(protocoloAtualizar.getByteCodigoBarra() == null) {
            
            protocoloAtualizar
                .setByteCodigoBarra(
                    DetranCodigoBarraHelper
                        .codigoBarra2de5(
                            DetranStringUtil.preencherEspaco(protocoloAtualizar.getNumeroProtocolo(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO)
                        )
                );
            
            new ProtocoloRepositorio().update(em, protocoloAtualizar);
        }
        
    }
}
