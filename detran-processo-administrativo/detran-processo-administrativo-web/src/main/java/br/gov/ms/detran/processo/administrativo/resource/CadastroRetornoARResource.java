package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.NotificacaoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAAndamentoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.NotificacaoProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoARDetalheWrapper;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreioDevolucao;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

@ManagedBean
@Path("cadastroretornoars")
public class CadastroRetornoARResource extends PaResource<NotificacaoProcessoAdministrativoWrapper, NotificacaoProcessoAdministrativoCriteria>{

    private static final Logger LOG = Logger.getLogger(CadastroRetornoARResource.class);

   private IPAAndamentoService iAndamentoServiceLab;
    
    public IPAAndamentoService getPAAndamentoServiceLab() {
        if (iAndamentoServiceLab == null) {
            iAndamentoServiceLab = (IPAAndamentoService) JNDIUtil.lookup("ejb/PAAndamentoService");
        }
        return iAndamentoServiceLab;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        NotificacaoProcessoAdministrativoCriteria c = (NotificacaoProcessoAdministrativoCriteria)criteria;
        
        view.addEntity(getProcessoAdministrativoService().buscarDadosRetornoAR(c.getObjetoCorreios()));
        getEntity().getEntidade().setEndereco(null);
        getEntity().getCorrespondenciaCorreio().setNomeRecebedor("");
        getEntity().getCorrespondenciaCorreio().setDocumentoRecebedor("");
    }

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {
        List<ListSelectItem> listSelectItem = new ArrayList<>();
        List<CorrespondenciaCorreioDevolucao> situacoes = getProcessoAdministrativoService().buscarTodos(CorrespondenciaCorreioDevolucao.class);
        
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(situacoes, "", "situacao", " - ","motivo", "descricao"));

        return listSelectItem;
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {

        RetornoARDetalheWrapper detalheWrapper = new RetornoARDetalheWrapper();
        NotificacaoProcessoAdministrativoWrapper wrapper = (NotificacaoProcessoAdministrativoWrapper) entidade;
        
        
        if(wrapper.getCorrespondenciaCorreio().getDataChegadaDestino().after(Calendar.getInstance().getTime())){
            DetranWebUtils.applicationMessageException("A data de Recebimento/Devolução não pode ser maior que a data atual.");
        }

        detalheWrapper.setNumeroProcesso(wrapper.getEntidade().getProcessoAdministrativo().getNumeroProcesso());
        detalheWrapper.setNumeroNotificacao(wrapper.getEntidade().getNumeroNotificacao());

        detalheWrapper.setDataEntregaOuDevolucao(wrapper.getCorrespondenciaCorreio().getDataChegadaDestino());
        detalheWrapper.setNomeRecebedor(wrapper.getCorrespondenciaCorreio().getNomeRecebedor());
        detalheWrapper.setNumeroDocumentoRecebedor(wrapper.getCorrespondenciaCorreio().getDocumentoRecebedor());
        detalheWrapper.setMotivoDevolucao("0" + wrapper.getCorrespondenciaCorreioDevolucao().getMotivo().toString());

        getProcessoAdministrativoService().atualizarCorrespondencia(detalheWrapper, Calendar.getInstance().getTime());
        try {
            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

            ProcessoAdministrativo pa = getProcessoAdministrativoService()
                    .getProcessoAdministrativo(wrapper.getEntidade().getProcessoAdministrativo().getNumeroProcesso());

            ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper = new ExecucaoAndamentoEspecificoWrapper(
                    pa,
                    usuarioLogado.getOperador().getUsuario().getId(),
                    null, null
            );

            PAFluxoFase fluxoFase
                    = (PAFluxoFase) getProcessoAdministrativoService()
                            .getFluxoFaseDoProcessoAdministrativo(andamentoEspecificoWrapper.getProcessoAdministrativo());

            if (TipoAndamentoEnum.AUTOMATICO.equals(fluxoFase.getTipoAndamento())) {
                getPAAndamentoServiceLab().executa(andamentoEspecificoWrapper);
            }
        } catch (AppException e) {

        }
    }
    
}