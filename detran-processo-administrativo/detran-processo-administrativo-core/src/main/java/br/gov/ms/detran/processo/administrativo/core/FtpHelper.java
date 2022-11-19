package br.gov.ms.detran.processo.administrativo.core;

import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DtnFTPClient;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.entidade.layout.ConfiguracaoFtp;

public class FtpHelper {

    private static final Logger LOG = Logger.getLogger(FtpHelper.class);

    static IApoioService apoioService;

    static IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    /**
     * @param nomeArquivo
     * @throws AppException
     */
    public static void gravarArquivoPasta(String nomeArquivo, String nomeFtp, TipoExtensaoArquivoEnum extensao) throws AppException {

        ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome(nomeFtp);

        if (configuracaoServer == null) {
            DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor " + nomeFtp);
        }

        DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

        boolean doneServer
                = clienteServidorLocal
                .upload(
                        nomeArquivo,
                        configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo).concat(extensao.toString()
                        ),
                        true
                );

        if (!doneServer) {
            LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);
        }
    }

    /**
     * @param nomeArquivo
     * @throws AppException
     */
    public static void gravarArquivoPasta(String pathPasta, String nomeArquivo, String nomeFtp, TipoExtensaoArquivoEnum extensao) throws AppException {

        ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome(nomeFtp);

        if (configuracaoServer == null) {
            DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor " + nomeFtp);
        }

        DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

        boolean doneServer
                = clienteServidorLocal
                .upload(
                        pathPasta,
                        configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo).concat(extensao.toString()
                        ),
                        true
                );

        if (!doneServer) {
            LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);
        }
    }
}
