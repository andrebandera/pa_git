package br.gov.ms.detran.processo.administrativo.constantes;

public interface PAAndamentoProcessoConstante {

    public interface INSTAURACAO {

        public static final Integer INSTAURAR_PROCESSO = 114;

        public static final Integer CONFIRMAR_INSTAURACAO_ENVIO_89 = 115;

        public static final Integer GERAR_JSON = 116;

        public static final Integer ATUALIZAR_INFRACOES_INSTAURACAO = 117;

        public static final Integer CONFIRMAR_INSTAURACAO = 118;

    }

    public interface NOTIFICACAO_INSTAURACAO {

        public static final Integer AGUARDAR_GERACAO_NOTIFICACAO_INSTAURACAO = 15;

        public static final Integer GERAR_NOTIFICACAO_INSTAURACAO = 20;

        public static final Integer GERAR_PDF_NOTIFICACAO_INSTAURACAO = 119;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO = 120;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO = 121;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_INSTAURACAO = 122;

        public static final Integer CONFIRMAR_NOTIFICACAO_INSTAURACAO = 123;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_INSTAURACAO = 21;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_INSTAURACAO = 124;

        public static Integer AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO = 231;

        public static Integer CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO = 232;
    }

    public interface NOTIFICACAO_PENALIZACAO {

        /**
         * Notificação Penalização. *
         */
        public static final Integer AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO = 26;

        public static final Integer GERAR_NOTIFICACAO_PENALIZACAO = 30;

        public static final Integer GERAR_PDF_NOTIFICACAO_PENALIZACAO = 125;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO = 126;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO = 127;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_PENALIZACAO = 128;

        public static final Integer CONFIRMAR_NOTIFICACAO_PENALIZACAO = 129;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_PENALIZACAO = 31;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_PENALIZACAO = 130;

        public static Integer AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO = 233;

        public static Integer CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO = 234;
    }

    public interface NOTIFICACAO_ENTREGA_CNH {

        /**
         * Notificação Entrega CNH. *
         */
        public static final Integer AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH = 39;

        public static final Integer GERAR_NOTIFICACAO_ENTREGA_CNH = 40;

        public static final Integer GERAR_PDF_NOTIFICACAO_ENTREGA_CNH = 131;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH = 132;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH = 133;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_ENTREGA_CNH = 134;

        public static final Integer CONFIRMAR_NOTIFICACAO_ENTREGA_CNH = 135;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH = 41;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_ENTREGA_CNH = 136;

        public static Integer AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH = 235;

        public static Integer CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH = 236;
    }

    public interface CUMPRIMENTO_PENA {

        /**
         * Cumprimento de Pena. *
         */
        public static final Integer INCLUIR_BLOQUEIO_WEB = 43;

        public static final Integer INCLUIR_BLOQUEIO_BCA = 44;

        public static final Integer CONFIRMAR_BLOQUEIO = 137;

        public static final Integer AGUARDAR_ENTREGA_CNH = 48;

        public static final Integer ATUALIZAR_BLOQUEIO_BCA = 45;

        public static final Integer CONFIRMAR_ENTREGA_CNH = 138;

        public static final Integer AGUARDAR_REALIZACAO_PROVA_CURSO = 60;

        public static final Integer CONFIRMAR_PROVA_CURSO = 139;

        public static final Integer AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH = 64;

        public static final Integer DESBLOQUEAR_BCA = 70;

        public static final Integer CONFIRMAR_DESBLOQUEIO = 140;

        public static final Integer CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA = 72;

        public static final Integer PROCESSO_ARQUIVADO_PENA_CUMPRIDA = 80;

        public static final Integer ATUALIZAR_BLOQUEIO_WEB = 183;

        public static final Integer DESBLOQUEAR_WEB = 184;

        public static final Integer ATUALIZAR_BLOQUEIO_BCA_PJU = 187;
    }

    public interface EDITAL_NOTIFICACAO {

        /**
         * Edital Notificações. *
         */
        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_INSTAURACAO = 22;

        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_PENALIZACAO = 32;

        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_ENTREGA_CNH = 42;

        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_DESENTRANHAMENTO = 54;

        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_CURSO_EXAME = 196;
    }

    public interface RECURSO_NOTIFICACAO_INSTAURACAO {

        /**
         * Recurso Notificação Instauração. *
         */
        public static final Integer INCLUIR_RECURSO_NOTIFICACAO_INSTAURACAO = 23;

        public static final Integer INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_INSTAURACAO = 141;

        public static final Integer EM_JULGAMENTO_RECURSO_NOT_INSTAURACAO = 24;

        public static final Integer ACOLHER_RECURSO_NOTIFICACAO_INSTAURACAO = 25;

    }

    public interface RECURSO_NOTIFICACAO_PENALIZACAO {

        /**
         * Recurso Notificação Penalização. *
         */
        public static final Integer INCLUIR_RECURSO_NOTIFICACAO_PENALIZACAO = 33;

        public static final Integer INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_PENALIZACAO = 142;

        public static final Integer EM_JULGAMENTO_RECURSO_NOT_PENALIZACAO = 34;

        public static final Integer ACOLHER_RECURSO_NOTIFICACAO_PENALIZACAO = 35;

    }

    public interface RECURSO_NOTIFICACAO_ENTREGA_CNH {

        /**
         * Recurso Notificação Entrega CNH. *
         */
        public static final Integer INCLUIR_RECURSO_NOTIFICACAO_ENTREGA_CNH = 36;

        public static final Integer INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_ENTREGA_CNH = 143;

        public static final Integer EM_JULGAMENTO_RECURSO_NOT_ENTREGA_CNH = 37;

        public static final Integer ACOLHER_RECURSO_NOTIFICACAO_ENTREGA_CNH = 38;

    }

    public interface RECURSO_NOTIFICACAO_DESENTRANHAMENTO {

        /**
         * Recurso Notificação Desentranhamento. *
         */
        public static final Integer RECURSO_NOT_DESENTRANHAMENTO = 177;

        public static final Integer RECURSO_NOT_DESENTRANHAMENTO_RESULTADO_BPMS = 178;

        public static final Integer EM_JUG_RECURSO_NOT_DESENTRANHAMENTO = 179;

        public static final Integer ACOLHER_RECURSO_NOT_DESENTRANHAMENTO = 180;
    }

    public interface CANCELAMENTO_RECURSO {

        /**
         * Cancelamento Recurso. *
         */
        public static final Integer CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_INSTAURACAO = 144;

        public static final Integer CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_PENALIZACAO = 145;

        public static final Integer CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_ENTREGA_CNH = 146;

        public static final Integer CANCELAMENTO_RECURSO_NOT_DESENTRANHAMENTO_BPMS = 181;
    }

    public interface DESISTENCIA {

        /**
         * Desistencia - Entrega CNH. *
         */
        public static final Integer CONFIRMAR_DESISTENCIA_ENTREGA_CNH = 147;

        /**
         * Desistencia Recurso Instauração/Penalização Confirmação BPMS. *
         */
        public static final Integer DESISTIR_RECURSO_INSTAURACAO_PENALIZACAO = 185;
        public static final Integer CONFIRMAR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO = 186;
    }

    public interface NOTIFICACAO_ACOLHIMENTO {

        /**
         * Notificação Acolhimento. *
         */
        public static final Integer AGUARDAR_GERACAO_NOTIFICACAO_ACOLHIMENTO = 46;

        public static final Integer GERAR_PDF_NOTIFICACAO_ACOLHIMENTO = 148;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO = 149;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO = 150;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_ACOLHIMENTO = 151;

        public static final Integer CONFIRMAR_NOTIFICACAO_ACOLHIMENTO = 152;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO = 50;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_ACOLHIMENTO = 153;

        public static final Integer CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO = 73;

        public static final Integer PROCESSO_ARQUIVADO_RECURSO_PROVIDO = 97;
    }

    public interface EDITAL_NOTIFICACAO_ACOLHIMENTO {

        /**
         * Edital Notificação Acolhimento. *
         */
        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_ACOLHIMENTO = 51;
    }

    public interface NOTIFICACAO_NAO_CONHECIMENTO {

        /**
         * Notificação Não Conhecimento. *
         */
        public static final Integer GERAR_NOTIFICACAO_NAO_CONHECIMENTO = 154;

        public static final Integer GERAR_PDF_NOTIFICACAO_NAO_CONHECIMENTO = 155;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO = 156;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO = 157;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_NAO_CONHECIMENTO = 158;
    }

    public interface NOTIFICACAO_DESENTRANHAMENTO {

        public static final Integer GERAR_NOTIFICACAO_DESENTRANHAMENTO = 166;
        
        public static final Integer CALCULAR_REGRA_CONCATENACAO_BLOQUEIO = 244;

        public static final Integer GERAR_PDF_NOTIFICACAO_DESENTRANHAMENTO = 167;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO = 168;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO = 169;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_DESENTRANHAMENTO = 170;

        public static final Integer CONFIRMAR_NOTIFICACAO_DESENTRANHAMENTO = 171;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_DESENTRANHAMENTO = 53;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_DESENTRANHAMENTO = 172;
    }

    public interface ARQUIVAMENTO {

        /**
         * Arquivamento. *
         */
        public static final Integer PROCESSO_ARQUIVADO_PENA_ANULADA = 90;

        public static final Integer CONFIRMAR_ARQUIVAMENTO_PROCESSO = 163;

        public static final Integer PROCESSO_ARQUIVADO_MANUALMENTE = 164;

        public static final Integer DESBLOQUEAR_WEB_PENA_ANULADA = 210;
    }

    public interface SUSPENSO {

        /**
         * Suspenso. *
         */
        public static final Integer SUSPENSO_POR_APENSADO_STANDBY = 100;

        public static final Integer PROCESSO_EM_STANDBY = 98;

        public static final Integer CONFIRMAR_SUSPENSAO = 207;

        public static final Integer PROCESSO_SUSPENSO = 161;

        public static final Integer CONFIRMAR_RETIRADA_DA_SUSPENSAO = 162;
    }

    public interface CANCELAMENTO {

        public static final Integer CONFIRMAR_CANCELAMENTO_PROCESSO = 159;

        public static final Integer PROCESSO_CANCELADO_MANUALMENTE = 165;
    }

    public interface APENSADO {

        public static final Integer CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO = 173;
    }

    public interface CASSADO {

        public static final Integer CONFIRMAR_ARQUIVAMENTO_PROCESSO_CASSADO = 175;
    }

    public interface ESPECIFICO_CORRECAO {

        public static final Integer BLOQUEIO_DATA_INICIO_PENALIDADE_INCORRETO_BCA = 1999;
        public static final Integer ARQUIVAR_PROCESSO_PRESCRITO = 217;
    }

    public interface NOTIFICACAO_CURSO_EXAME {

        public static final Integer GERAR_NOTIFICACAO_CURSO_EXAME = 188;

        public static final Integer GERAR_PDF_NOTIFICACAO_CURSO_EXAME = 189;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME = 190;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME = 191;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_CURSO_EXAME = 192;

        public static final Integer CONFIRMAR_NOTIFICACAO_CURSO_EXAME = 193;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_CURSO_EXAME = 194;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_CURSO_EXAME = 195;

    }

    public interface PROCESSO_JURIDICO {

        public static final Integer INCLUIR_BLOQUEIO_BCA_PJU = 2;

        public static final Integer GERAR_JSON_PJU = 208;

        public static final Integer CUMPRIR_PENALIDADE = 211;

        public static final Integer GERAR_PENALIDADE = 212;

        public static final Integer CONFIRMAR_ENTREGA_CNH_CARTORIO = 213;

        public static final Integer CONFIRMAR_DESBLOQUEIO_CARTORIO = 214;

        public static final Integer GERAR_NOTIFICACAO_DESENTRANHAMENTO_PJU = 215;

        public static final Integer CONFIRMAR_DESBLOQUEIO_CANDIDATO_CIDADAO_PJU = 216;

    }

    public interface NOTIFICACAO_JARI {

        public static final Integer GERAR_PDF_NOTIFICACAO_JARI = 198;

        public static final Integer GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI = 199;

        public static final Integer ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI = 200;

        public static final Integer TRANSFERIR_SGI_NOTIFICACAO_JARI = 201;

        public static final Integer CONFIRMAR_NOTIFICACAO_JARI = 202;

        public static final Integer AGUARDAR_AR_NOTIFICACAO_JARI = 203;

        public static final Integer CONFIRMAR_RETORNO_AR_NOTIFICACAO_JARI = 204;

        public static final Integer AGUARDAR_EDITAL_NOTIFICACAO_JARI = 205;

        public static final Integer AGUARDAR_GERACAO_NOTIFICACAO_JARI = 206;

        public static Integer AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_JARI = 237;

        public static Integer CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_JARI = 238;
    }

    public interface NOTIFICACAO_CETRAN {

        public static Integer AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_CETRAN = 224;

        public static Integer CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_CETRAN = 225;
    }

    public static final Integer ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018 = 240;
    
    public static final Integer ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS = 241;
    
    public static final Integer ATUALIZACAO_BLOQUEIO_BCA = 242;
    
    public static final Integer EMISSAO_NOTIFICACAO_ARQUIVAMENTO_INDEVIDO = 243;
}
