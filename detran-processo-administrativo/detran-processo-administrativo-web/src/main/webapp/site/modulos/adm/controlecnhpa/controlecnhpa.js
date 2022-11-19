/**
 * @author: Lillydi
 */
angular.module('controlecnhpa', ['ngRoute'])

.constant("controlecnhpaConfig", {
    name: 'controlecnhpa',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/controlecnhpas/search',
        "gravar": 'detran-processo-administrativo/resource/controlecnhpas/salvar',
        "desbloquear": 'detran-processo-administrativo/resource/controlecnhpas/desbloquear',
        "desativar": 'cnhsituacaoentregas/',
        "novo": 'detran-processo-administrativo/resource/controlecnhpas/new',
        "carregarpenalidade":  'detran-processo-administrativo/resource/controlecnhpas/carregardadoscontrolecnh',
        "exportarprotocolo": 'detran-processo-administrativo/resource/controlecnhpas/exportarprotocolo',
        "integrarinfoprova": 'detran-processo-administrativo/resource/controlecnhpas/integrarinfoprova'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "situacao.id", hidden: "hide"},
        {field: "versaoRegistro", hidden: "hide"},
        {field: "situacao.acao", hidden: "hide"},
        {field: "situacao.cnhControle.id", hidden: "hide"}, 
        {field: "entidade.protocolo.numeroProcesso.numeroProcessoMascarado", displayName: "controlecnhpa.label.numeroProcesso"}, 
        {field: "dataFimPenalidade", displayName: "controlecnhpa.label.dataFim"}, 
        {field: "situacao.cnhControlePessoa.cpfEntrega", displayName: "controlecnh.label.cpf"}, 
        {field: "situacao.cnhControlePessoa.nomeEntrega", displayName: "controlecnh.label.nome"}, 
        {field: "situacao.cnhControle.numeroRegistro", displayName: "controlecnh.label.numeroRegistro"}, 
        {field: "situacao.cnhControle.numeroCnh", displayName: "controlecnh.label.numeroCnh"}, 
        {field: "situacao.postoAtendimento.descricao", displayName: "controlecnh.label.postoAtendimento"},
        {field: "acaoLabel", displayName: "controlecnh.label.acao"},
        {field: "validadeCnhLabel", displayName: "controlecnh.label.validadeCnh"},
        {field: "actions", actions: [
            {name: "desbloquear", icon: "unlock-alt", btn: "success", title: "Desbloquear CNH", typeicon:'fa'},
            {name: "entrega", icon: "global.btn.download.icon", btn: "global.btn.download.btn", title: "Protocolo Entrega"},
            {name: "devolucao", icon: "global.btn.download.icon", btn: "warning", title: "Protocolo Devolução"},
            {name: "integrarinfoprova", icon: "global.btn.atualizar.icon", btn: "success", title: "Integração para informações de prova"}
        ]}
    ],
    filesPath :  '/detran-processo-administrativo/site/modulos/adm/controlecnhpa',
    urlPath : '/adm/controlecnhpa',
    i18nextResources: {
        "controlecnhpa": {
            "titulo": "Controle CNH - Processo Administrativo",
            "avisoDataInicio": "Se houver mais de um processo cumprindo penalidade, a data de início será a do mais antigo.",
            "label": {
                "numeroProcesso":"Nº Processo",
                "responsavel":"Responsável Protocolo",
                "representanteLegal":"Representante Legal",
                "forma":"Forma Protocolo",
                "movimentoFormaEntrega":"Tipo Entrega CNH",
                "portariaPenalidade":"Nº Portaria Penalidade",
                "dataInicio":"Data Início Penalidade",
                "dataFim":"Data Fim Penalidade",
                "postoAtendimento":"Posto Atendimento Possível Devolução CNH",
                "postoAtendimentoRecolhimento":"Posto Atendimento Recolhimento",
                "tempoPenalidade":"Tempo Penalidade PA (Meses)",
                "tempoPenalidadePJU":"Tempo Penalidade PJU (Dias)",
                "tipoMovimento":"Movimento",
                "telefone":"Telefone para Contato",
                "observacao": "Observação"
            }
        },
        "controlecnh": {
            "titulo": "Controle CNH",
            "label": {
                "cpf": "CPF",
                "pid": "Número PID",
                "nome": "Nome",
                "acao": "Situação CNH",
                "numeroRegistro": "Nº Registro",
                "numeroCnh": "Nº CNH",
                "motivo": "Motivo Cancelamento",
                "postoAtendimento": "Posto Atendimento",
                "formaEntrega": "Modo Recolhimento CNH",
                "observacao": "Observação",
                "dataEntrega": "Data Possível Liberação",
                "validadeCnh": "Validade CNH"
            }
        }
    }
})

.config(["$routeProvider", 'controlecnhpaConfig', function($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;

    $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'controlecnhpa.titulo'})
    .when(urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when(urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'})
    .when(urlPath + '/editar', {templateUrl: path + '/form.html', controller: name + 'Edit', label: 'global.label.breadcrumb.editar'});
}])

.controller('controlecnhpaCtrl', ["$scope", "$injector", "$controller", 'controlecnhpaConfig',
    function($scope, $injector, $controller, config) {        
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
        
        $scope.verificaPermissaoControleCnh = function(param){
            
            var cpf = param;
            
            $scope.carregar('detran-processo-administrativo/resource/usuarioacessopa/verificarpermissao', {cpfCondutor:cpf}).then(
                function(retorno){
                    
                    $scope.usuarioAcessoPA = retorno.objectResponse;
                    $scope.apresentarBotaoDesbloquearCnh = retorno.objectResponse.apresentarBotaoDesbloquearCnh;
                    $scope.data.entidade.formaEntrega = $scope.usuarioAcessoPA.formaEntregaControle;
                    $scope.data.entidade.modoEntrega = $scope.usuarioAcessoPA.formaEntrega;
                    $scope.data.entidade.templateProtocolo.formaProtocolo = $scope.usuarioAcessoPA.formaProtocolo;
                    $scope.data.entidade.cnhControle.postoAtendimentoDevolucao ={
                                            id:$scope.usuarioAcessoPA.postoAtendimento.id,
                                            versaoRegistro:$scope.usuarioAcessoPA.postoAtendimento.versaoRegistro
                    };
                    $scope.data.entidade.postoAtendimento ={
                                            id:$scope.usuarioAcessoPA.postoAtendimento.id,
                                            versaoRegistro:$scope.usuarioAcessoPA.postoAtendimento.versaoRegistro
                    };
                    $scope.dataModal.postoDescricao = $scope.usuarioAcessoPA.postoAtendimento.descricao;
                    $scope.dataModal.postoRecolhimentoDescricao = $scope.usuarioAcessoPA.postoAtendimento.descricao;
                    
                    $scope.load();
                }
            );
        };
    }
])

.controller('controlecnhpaList', ['$scope', '$controller', 'controlecnhpaConfig', function($scope, $controller, config) {
        $scope.page = 'list';
        $controller('controlecnhpaCustomCtrl', {$scope: $scope});
        $scope.verificaPermissaoControleCnh();
    }
])

.controller('controlecnhpaCreate', ['$scope', '$controller', function($scope, $controller) {
        $scope.page = 'create';
        $controller('controlecnhpaCustomCtrl', {$scope: $scope});
    }
])

.controller('controlecnhpaView', ['$scope', '$controller', function($scope, $controller) {
        $scope.page = 'view';
        $controller('controlecnhpaCustomCtrl', {$scope: $scope});
    }
])

.controller('controlecnhpaEdit', ["$scope", "$controller", '$rootScope',
    function($scope, $controller, $rootScope) {
        $scope.page = 'edit';
        $controller('controlecnhpaCustomCtrl', {$scope: $scope});
    }
])

.controller('controlecnhpaCustomCtrl', ["$scope", "$controller", "controlecnhpaConfig", "$rootScope", "$injector", "detranModal", "growl", "dtnAttachFiles", "$rootScope", "$location",
    function($scope, ctrl, config, $rootScope, $injector, dtnModal, growl, dtnAttachFiles, $rootScope, $location) {
        
        ctrl('controlecnhpaCtrl', {$scope: $scope});
        
        
        $scope.appFiles = {};
        $scope.appFiles = new dtnAttachFiles();
        $scope.appFiles.setConfig($scope.config);
        
        $scope.dataModal = {};
        $scope.criteria = {};
        $scope.data = {
            movimento : {},
            processo:{},
            penalidade:{},
            protocolo:{},
            entidade:{
                cnhControlePessoa:{},
                cnhControle:{},
                templateProtocolo:{}
            }
        };
        
        $scope.showList = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.rulesToDisableButton = function (data) {
            var linha = data.linha;
            var button = data.button;
            var acao = _.result(_.find(linha, {field: 'situacao.acao'}), 'value');

            if (button.name === "excluir" || button.name === "desbloquear") {
                if (_.isEqual(acao, 'DEVOLUCAO')) 
                    return true;
                if(!$scope.apresentarBotaoDesbloquearCnh)
                    return true;
            }    
            if (button.name === "devolucao") {
                if (_.isEqual(acao, 'ENTREGA')) 
                    return true;
            }    
            return false;
        };

        $scope.filesPath = function(file) {

            if (angular.isUndefined(file)) return config.filesPath;

            return "" + config.filesPath + '/' + file + "";
        };
        
        $scope.$on('getAction' + config.name, function (e, linha) {
            var id = _.find(linha.data, {field: "id"}).value;
            var wrapper = {};
            if (linha.action === "entrega") {
                
                wrapper.entidade = {
                    id: id,
                    acao: 'ENTREGA'
                };
                exportar(wrapper);
                
            } else if (linha.action === "devolucao") {
                wrapper.entidade = {
                    id: id,
                    acao: 'DEVOLUCAO'
                };
                exportar(wrapper);
                 
            } else if (linha.action === "desbloquear") {
                wrapper.entidade = {
                    cnhControle:{
                        id: _.find(linha.data, {field: "situacao.cnhControle.id"}).value
                    }
                 };
                desbloquear(wrapper);
                
            } else if (linha.action == "integrarinfoprova") {
                
                var numeroProcesso = _.find(linha.data, {field: "entidade.protocolo.numeroProcesso.numeroProcessoMascarado"}).value;
                
                $scope.carregar(config.urls.integrarinfoprova, numeroProcesso).then(function(retorno){
                    
                    var view = new ModelView(retorno);
                    
                     if (retorno.error != "") {
                         detranUtil.showMsg(growl, retorno.error);
                     } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                         detranUtil.showMsgWarn(growl, retorno.warning);
                     } else {
                         showMsgSuccess(growl,retorno.message);
                     }
                });
            }
        });
        
        function exportar(wrapper){
            $scope.editar(config.urls.exportarprotocolo, wrapper).then(function (retorno) {

                if (!_.isEmpty(retorno.error)) {

                    detranUtil.showMsg(growl,retorno.error);

                } else {

                    var byteArquivo = retorno.objectResponse.byteArquivo;

                    if (detranUtil.ehBrancoOuNulo(byteArquivo)) {

                        detranUtil.showMsg(growl, "Arquivo inválido.");
                        $rootScope.loading = false;
                        return;
                    }

                    var item = { 
                        byteArquivo : byteArquivo,
                        extensao : "PDF",
                        usePdfJS : false,
                        nameModal : "Protocolo Entrega/Devolução",
                        orientation : 'landscape'
                    };

                    dtnAttachFiles().openFile(item, $scope);
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        }
        
        function desbloquear(wrapper){
            var modalConfiguration = {
                    tplUrl: config.filesPath + '/desbloquear.html',
                    config:config,
                    modalCtrl: 'desbloquearCnhaPAModalCtrl',
                    name: 'desbloquear',
                    args: {
                        obj: wrapper,
                        windowClass: "app-modal-window-lg",
                        adicionarReponsavel: function(){
                            $scope.adicionarResponsavel();
                        }
                    }
                };
            	
                if (!$scope.modalIsLoading) {
                    dtnModal.modalForm(modalConfiguration, $scope);
                }
                
                $scope.$on('modalCanceldesbloquear', function(event, retornoModal) {
                    $scope.load();
                });
        }
            
        $scope.adicionarResponsavel = function(){
            var modalResponsavelConfiguration = {
                    tplUrl: '/detran-processo-administrativo/site/modulos/adm/responsavelprot/representantelegal.html',
                    modalCtrl: 'responsavelProtocoloCtrl',
                    name: 'responsavelProtocolo',
                    args: {
    //                    obj: data,
                        config:config,
                        windowClass: "app-modal-window-lg",
                    }
                };

                if (!$scope.modalIsLoading) {
                    dtnModal.modalForm(modalResponsavelConfiguration, $scope);
                }
                
                $scope.$on('modalOkresponsavelProtocolo', function (event, retorno) {
                    $scope.data.representanteLegal = retorno;
                });
        };
        
        $scope.limparResponsavel = function(){  
            if(!detranUtil.ehBrancoOuNulo($scope.data))
                delete($scope.data.representanteLegal);
        };
        

        $scope.buscarProcessoAdministrativo = function (cpf, numeroCnh) {
            if (detranUtil.ehBrancoOuNulo(cpf) || detranUtil.ehBrancoOuNulo(numeroCnh))
                return;
            
            $scope.carregar(config.urls.carregarpenalidade, {cpf: cpf, cnh: numeroCnh}).then(function (retorno) {
                if (retorno.error != "") {
                    detranUtil.showMsg(growl, retorno.error);
                } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                    detranUtil.showMsgWarn(growl, retorno.warning);
                } else {
                    $scope.data = retorno.objectResponse;
                    
                    $scope.data.entidade.cnhControlePessoa.nomeEntrega = $scope.data.dadosCondutorBCA.nomeCondutor;
                    $scope.data.entidade.cnhControlePessoa.cpfEntrega = $scope.data.dadosCondutorBCA.cpf;
                    $scope.data.entidade.cnhControle.numeroRegistro = $scope.data.dadosCondutorBCA.numeroRegistro;
                    $scope.data.entidade.cnhControle.numeroCnh = $scope.data.dadosCondutorBCA.numeroCnh;
               
                    populaTabelaProcessosAguardandoEntrega();
                    populaTabelaProcessosJuridicosAguardandoEntrega();
                    populaTabelaProcessosDesistencia();
                    populaTabelaProcessosDesistenciaJ();
                    populaTabelaProcessosDesistenciaRecurso();
                    
                    $scope.verificaPermissaoControleCnh($scope.data.entidade.cnhControlePessoa.cpfEntrega);
                }
            });
        };
        
        /*
         * Posto Possivel Devolucao
         */
        $scope.typePosto = ['posto'];
        $scope.postoModal = function (type) {

            if (type == 'posto') {
                if ($scope.dataModal.postoDescricao == undefined 
                        || ($scope.dataModal.postoDescricao.length < 5 && $scope.dataModal.postoDescricao || $scope.dataModal.postoDescricao == '')) {
                    showMsgWarn(growl, "É necessário digitar no minimo 5 (cinco) caracteres.");
                    return;
                }
            };

            var modalConfiguration = {
                name: "PostoModal",
                resourcePath: 'postoatendimentos/search',
                params: {
                    descricao: $scope.dataModal.postoDescricao,
                    populateList: false
                },
                inputModel: 'dataModal.postoDescricao',
                inputHidden: 'data.entidade.cnhControle.postoAtendimentoDevolucao',
                args: {
                    "type": "table",
                    "model": [
                        {field: 'entidade.id', hidden: "hide"},
                        {field: 'entidade.versaoRegistro', hidden: "hide"},
                        {field: 'entidade.descricao', displayName: "Descrição"},
                    ],
                    "title": 'Posto de Atendimento'
                },
                fieldDisplayName: 'descricao',
                config: config
            };

            if (!$scope.modalIsLoading) {
                dtnModal.getModal(modalConfiguration, $scope);
            }
        };

        $scope.$on("selectedModalItemPostoModal", function (event, data) {
            
            if (angular.isUndefined(data))
                return;

            if (!angular.isUndefined(data.entidade)) {
                $scope.data.entidade.cnhControle.postoAtendimentoDevolucao = {
                            id: data.entidade.id,
                            versaoRegistro: data.entidade.versaoRegistro
                };
                $scope.dataModal.postoDescricao = data.entidade.descricao;
            } else {
                $scope.data.entidade.cnhControle.postoAtendimentoDevolucao = {
                            id: data.id,
                            versaoRegistro: data.versaoRegistro
                };
                $scope.dataModal.postoDescricao = data.descricao;
            }
        });

        $scope.$watch('dataModal.postoDescricao', function (newValue, oldValue) {
            if ((newValue != oldValue) && newValue == '' && $scope.data.entidade != undefined) {
                $scope.data.entidade.cnhControle.postoAtendimentoDevolucao = {};
            }
        });
        
        
        /*
         * Posto recolhimento CNH
         */
             $scope.postoRecolhimentoModal = function (type) {

            if (type == 'posto') {
                if ($scope.dataModal.postoRecolhimentoDescricao == undefined 
                        || ($scope.dataModal.postoRecolhimentoDescricao.length < 5 && $scope.dataModal.postoRecolhimentoDescricao 
                            || $scope.dataModal.postoRecolhimentoDescricao == '')) {
                    showMsgWarn(growl, "É necessário digitar no minimo 5 (cinco) caracteres.");
                    return;
                }
            };

            var modalConfiguration = {
                name: "PostoRecolhimentoModal",
                resourcePath: 'postoatendimentos/search',
                params: {
                    descricao: $scope.dataModal.postoRecolhimentoDescricao,
                    populateList: false
                },
                inputModel: 'dataModal.postoRecolhimentoDescricao',
                inputHidden: 'data.entidade.postoAtendimento',
                args: {
                    "type": "table",
                    "model": [
                        {field: 'entidade.id', hidden: "hide"},
                        {field: 'entidade.versaoRegistro', hidden: "hide"},
                        {field: 'entidade.descricao', displayName: "Descrição"},
                    ],
                    "title": 'Posto de Atendimento'
                },
                fieldDisplayName: 'descricao',
                config: config
            };

            if (!$scope.modalIsLoading) {
                dtnModal.getModal(modalConfiguration, $scope);
            }
        };

        $scope.$on("selectedModalItemPostoRecolhimentoModal", function (event, data) {
            
            if (angular.isUndefined(data))
                return;

            if (!angular.isUndefined(data.entidade)) {
                $scope.data.entidade.postoAtendimento = {
                            id: data.entidade.id,
                            versaoRegistro: data.entidade.versaoRegistro
                };
                $scope.dataModal.postoRecolhimentoDescricao = data.entidade.descricao;
            } else {
                $scope.data.entidade.postoAtendimento = {
                            id: data.id,
                            versaoRegistro: data.versaoRegistro
                };
                $scope.dataModal.postoRecolhimentoDescricao = data.descricao;
            }
        });

        $scope.$watch('dataModal.postoRecolhimentoDescricao', function (newValue, oldValue) {
            if ((newValue != oldValue) && newValue == '' && $scope.data.entidade != undefined) {
                $scope.data.entidade.postoAtendimento = {};
            }
        });
       
        $scope.$on('newClicked' + config.name, function (event) {
            $scope.obs = null;
            $scope.portariaPenalidade = null;
            $scope.criteria = {};
            
            $scope.data = {
                movimento : {},
                processo:{},
                penalidade:{},
                protocolo:{},
                entidade:{
                    cnhControlePessoa:{},
                    cnhControle:{},
                    templateProtocolo:{}
                }
            };
            delete($scope.data.processosParaDesistenciaSelecionados);
            delete($scope.data.processosParaDesistenciaRecursoSelecionados);
            delete($scope.data.processosParaDesistencia);
            delete($scope.data.processosParaDesistenciaComRecurso);
            populaTabelaProcessosDesistencia();
            populaTabelaProcessosDesistenciaJ();
            populaTabelaProcessosDesistenciaRecurso();
            
            populaTabelaProcessosAguardandoEntrega();
            
            populaTabelaProcessosJuridicosAguardandoEntrega();
            
//            $scope.verificaPermissaoControleCnh();
            $scope.dataModal = {}; 
        });

        $scope.preSave = function() {
//            $scope.data.protocolo.observacao = $scope.data.entidade.observacao;
            
           $rootScope.loading = true;
            $scope.gravar(config.urls.gravar, $scope.data, 'btnSaveUpdate').then(function(retorno) {
            if (retorno.error != "") {
                detranUtil.showMsg(growl,retorno.error);
                $rootScope.loading = false;

            }
            else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
            	detranUtil.showMsgWarn(growl,retorno.warning);
                $rootScope.loading = false;
            }
            else {

                /** Para a ação salvar atualizar a entity(Wapper ou Bean) **/
                $scope.data = retorno.entity[0];
                
                detranUtil.showMsgSuccess(growl,retorno.message);
                $rootScope.loading = false;
                $scope.clearData();
                $location.path(config.urlPath);

            }
        }, function(erro) {
            $rootScope.loading = false;
            detranUtil.showMsg(growl,erro.message);
        });
        };
        
        $scope.validateForm = function() {
            if (!$scope.dataChanged()) return true;
            
            if (($scope.data != null && $scope.data != '') && ($scope.data.entidade != null && $scope.data.entidade != '')) {
            
                if (($scope.data.entidade.cnhControlePessoa != null && $scope.data.entidade.cnhControlePessoa != '')
                        && ($scope.data.entidade.cnhControle != null && $scope.data.entidade.cnhControle != '')) {
                    
                    if (($scope.data.entidade.cnhControlePessoa.nomeEntrega !== null && $scope.data.entidade.cnhControlePessoa.nomeEntrega !== '')
                            && ($scope.data.entidade.cnhControlePessoa.cpfEntrega !== null && $scope.data.entidade.cnhControlePessoa.cpfEntrega !== '')
                            && ($scope.data.entidade.cnhControle.numeroRegistro !== null && $scope.data.entidade.cnhControle.numeroRegistro !== '')
                            && ($scope.data.entidade.cnhControle.numeroCnh !== null && $scope.data.entidade.cnhControle.numeroCnh !== '')
                            && ($scope.data.entidade.formaEntrega !== null && $scope.data.entidade.formaEntrega !== '')
                            && ($scope.data.entidade.observacao !== null && $scope.data.entidade.observacao !== '')
                            && ($scope.data.entidade.cnhControle.postoAtendimentoDevolucao !== null && $scope.data.entidade.cnhControle.postoAtendimentoDevolucao !== '')
                            && ($scope.data.entidade.postoAtendimento !== null && $scope.data.entidade.postoAtendimento !== '')
                            && ($scope.data.entidade.modoEntrega !== null && $scope.data.entidade.modoEntrega !== '')
                            && ($scope.data.entidade.templateProtocolo.formaProtocolo !== null && $scope.data.entidade.templateProtocolo.responsavelProtocolo !== '')
                            && ($scope.data.entidade.templateProtocolo.responsavelProtocolo !== null && $scope.data.entidade.templateProtocolo.responsavelProtocolo !== '')) {

                        return false;
                    }
                }
            }

            return true;
        };
    
        function populaTabelaProcessosAguardandoEntrega() {

            $scope.configTableProcessos48 = {
                tableName: "TableProcessos48",
                name: "processos48Table",
                urls: {
                },
                model: [
                    {field: "id", hidden: "hide"},
                    {field: "versaoRegistro", hidden: "hide"},
                    {field: "processo.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "numeroPortariaMascarado", displayName: "Número Portaria Penalidade"},
                    {field: "tempoPenalidadeLabel", displayName: "Prazo"}
                ],
                //                itemsPerPage: 7,
            };

            $scope.configTableProcessos48.customData = $scope.data.processosAguardandoEntrega;
            $scope.hasData48 = !_.isEmpty($scope.data.processosAguardandoEntrega);

        };
        
        function populaTabelaProcessosJuridicosAguardandoEntrega() {

            $scope.configTableProcessos48J = {
                tableName: "TableProcessos48J",
                name: "processos48JTable",
                urls: {
                },
                model: [
                    {field: "id", hidden: "hide"},
                    {field: "versaoRegistro", hidden: "hide"},
                    {field: "processo.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "numeroPortariaMascarado", displayName: "Número Portaria Penalidade"},
                    {field: "tempoPenalidadeLabel", displayName: "Prazo"}
                ],
                //                itemsPerPage: 7,
            };

            $scope.configTableProcessos48J.customData = $scope.data.processosJuridicosEntrega;
            $scope.hasData48J = !_.isEmpty($scope.data.processosJuridicosEntrega);

        };
        
        function populaTabelaProcessosDesistencia() {
            
            $scope.data.processosParaDesistenciaSelecionados = [];
            
            $scope.configTableProcessosDesistencia = {
                tableName: "TableProcessosDesistencia",
                name: "processosDesistenciaTable",
                urls: {
                },
                model: [
                    {"field": "checkbox", "displayName": "", "hidden": "hide", "checkboxValuePosition": "1"},
                    {field: "processoAdministrativoId", hidden: "hide"},
                    {field: "versaoRegistro", hidden: "hide"},
                    {field: "processo.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "numeroPortariaMascarado", displayName: "Número Portaria Penalidade"},
                    {field: "tempoPenalidadeLabel", displayName: "Prazo"}
                ],
                tableWithCheckboxes: true,
                checkBoxesIdField: 'processoAdministrativoId'
            };
            
            $scope.configTableProcessosDesistencia.customData = $scope.data.processosParaDesistencia;
            if($scope.page != "view"){
//                $scope.configTableProcessosDesistencia.template = $rootScope.urlPartials + "directiveTableComCheckboxes.html";
                $scope.configTableProcessosDesistencia.template = config.filesPath + "/tableComCeckboxes.html";
            }
            $scope.hasDataDesistencia = !_.isEmpty($scope.data.processosParaDesistencia);
        };
        
        $scope.$on('afterAddRemoveCheckboxes' + 'processosDesistenciaTable', function(event, item) {
            var encontrou = _.find($scope.data.processosParaDesistenciaSelecionados, function(n) {
                return n.processoAdministrativoId === item.processoAdministrativoId; 
            });

            if(encontrou) {
                var subtraiPenalidade = (parseInt($scope.data.tempoPenalidade, 10) - parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidade = subtraiPenalidade.toString();
                _.remove($scope.data.processosParaDesistenciaSelecionados, function(n) {
                    return n.processoAdministrativoId === item.processoAdministrativoId;
                });
            } else {
                var somaPenalidade = (parseInt($scope.data.tempoPenalidade, 10) + parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidade = somaPenalidade.toString();
                $scope.data.processosParaDesistenciaSelecionados.push(_.find($scope.data.processosParaDesistencia, {processoAdministrativoId: item.processoAdministrativoId}));
            }
            $scope.atualizarDatasPenalidade();
        });
        
        function populaTabelaProcessosDesistenciaJ() {
            
            $scope.data.processosJuridicosOutrosAndamentosSelecionados = [];
            
            $scope.configTableProcessosDesistenciaJ = {
                tableName: "TableProcessosDesistenciaJ",
                name: "processosDesistenciaJTable",
                urls: {
                },
                model: [
                    {"field": "checkbox", "displayName": "", "hidden": "hide", "checkboxValuePosition": "1"},
                    {field: "processoAdministrativoId", hidden: "hide"},
                    {field: "versaoRegistro", hidden: "hide"},
                    {field: "processo.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "numeroPortariaMascarado", displayName: "Número Portaria Penalidade"},
                    {field: "tempoPenalidadeLabel", displayName: "Prazo"}
                ],
                tableWithCheckboxes: true,
                checkBoxesIdField: 'processoAdministrativoId'
            };
            
            $scope.configTableProcessosDesistenciaJ.customData = $scope.data.processosJuridicosOutrosAndamentos;
            if($scope.page != "view"){
//                $scope.configTableProcessosDesistencia.template = $rootScope.urlPartials + "directiveTableComCheckboxes.html";
                $scope.configTableProcessosDesistenciaJ.template = config.filesPath + "/tableComCeckboxes.html";
            }
            $scope.hasDataDesistenciaJ = !_.isEmpty($scope.data.processosJuridicosOutrosAndamentos);
        };
        
        $scope.$on('afterAddRemoveCheckboxes' + 'processosDesistenciaJTable', function(event, item) {
            var encontrou = _.find($scope.data.processosJuridicosOutrosAndamentosSelecionados, function(n) {
                return n.processoAdministrativoId === item.processoAdministrativoId; 
            });
            console.log(item.prazoIndeterminado);
        if(detranUtil.ehBrancoOuNulo(item.prazoIndeterminado) || item.prazoIndeterminado == 'NAO'){
            if(encontrou) {
                var subtraiPenalidade = (parseInt($scope.data.tempoPenalidadePJU, 10) - parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidadePJU = subtraiPenalidade.toString();
                _.remove($scope.data.processosJuridicosOutrosAndamentosSelecionados, function(n) {
                    return n.processoAdministrativoId === item.processoAdministrativoId;
                });
            } else {
                var somaPenalidade = (parseInt($scope.data.tempoPenalidadePJU, 10) + parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidadePJU = somaPenalidade.toString();
                $scope.data.processosJuridicosOutrosAndamentosSelecionados.push(_.find($scope.data.processosJuridicosOutrosAndamentos, {processoAdministrativoId: item.processoAdministrativoId}));
            }
            $scope.atualizarDatasPenalidade();
        }else{
             if(encontrou) {
                 _.remove($scope.data.processosJuridicosOutrosAndamentosSelecionados, function(n) {
                    return n.processoAdministrativoId === item.processoAdministrativoId;
                });
             }else{
                 $scope.data.processosJuridicosOutrosAndamentosSelecionados.push(
                                _.find($scope.data.processosJuridicosOutrosAndamentos, {processoAdministrativoId: item.processoAdministrativoId}));
             }
        }
        });
        
        function populaTabelaProcessosDesistenciaRecurso() {
            
            $scope.data.processosParaDesistenciaRecursoSelecionados = [];
            
            $scope.configTableProcessosDesistenciaRecurso = {
                tableName: "TableProcessosDesistenciaRecurso",
                name: "processosDesistenciaRecursoTable",
                urls: {
                },
                model: [
                    {"field": "checkbox", "displayName": "", "hidden": "hide", "checkboxValuePosition": "1"},
                    {field: "processoAdministrativoId", hidden: "hide"},
                    {field: "versaoRegistro", hidden: "hide"},
                    {field: "processo.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "numeroPortariaMascarado", displayName: "Número Portaria Penalidade"},
                    {field: "tempoPenalidadeLabel", displayName: "Prazo"}
                ],
                tableWithCheckboxes: true,
                checkBoxesIdField: 'processoAdministrativoId'
            };
            
            $scope.configTableProcessosDesistenciaRecurso.customData = $scope.data.processosParaDesistenciaComRecurso;
            console.log($scope.configTableProcessosDesistenciaRecurso.customData);
            if($scope.page != "view"){
//                $scope.configTableProcessosDesistencia.template = $rootScope.urlPartials + "directiveTableComCheckboxes.html";
                $scope.configTableProcessosDesistenciaRecurso.template = config.filesPath + "/tableComCeckboxes.html";
            }
            $scope.hasDataDesistenciaRecurso = !_.isEmpty($scope.data.processosParaDesistenciaComRecurso);
        };
        
        $scope.$on('afterAddRemoveCheckboxes' + 'processosDesistenciaRecursoTable', function(event, item) {
            var encontrou = _.find($scope.data.processosParaDesistenciaRecursoSelecionados, function(n) {
                return n.processoAdministrativoId === item.processoAdministrativoId; 
            });

            if(encontrou) {
                var subtraiPenalidade = (parseInt($scope.data.tempoPenalidade, 10) - parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidade = subtraiPenalidade.toString();
                _.remove($scope.data.processosParaDesistenciaRecursoSelecionados, function(n) {
                    return n.processoAdministrativoId === item.processoAdministrativoId;
                });
            } else {
                var somaPenalidade = (parseInt($scope.data.tempoPenalidade, 10) + parseInt(item.tempoPenalidade, 10));
                $scope.data.tempoPenalidade = somaPenalidade.toString();
                $scope.data.processosParaDesistenciaRecursoSelecionados.push(_.find($scope.data.processosParaDesistenciaComRecurso, {processoAdministrativoId: item.processoAdministrativoId}));
            }
            $scope.atualizarDatasPenalidade();
        });
        
        
        $scope.atualizarDatasPenalidade = function(){
            var parts = $scope.data.entidade.dataEntrega.split("/");
            if(parts.length === 3){
                var novaData = new Date(parts[2], parts[1] - 1, parts[0]);
                
                console.log(novaData.toLocaleDateString());
                novaData.setDate(novaData.getDate() + parseInt($scope.data.tempoPenalidadePJU, 10));
                console.log(novaData.toLocaleDateString());
                novaData.setMonth(novaData.getMonth() + parseInt($scope.data.tempoPenalidade, 10));
                $scope.data.dataFimPenalidade = novaData.toLocaleDateString();
            }
        };
    }
])

.controller('desbloquearCnhaPAModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", "args", "$injector", "detranModal",
    function($scope, $modalInstance, growl, srvRest, args, $injector, dtnModal) {
        
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.page = 'create';
//        $scope.data = args.obj;
        $scope.dataModal = {};
        
        var movimento = {cnhControle: args.obj.entidade.cnhControle.id};
        
        srvRest.editar('detran-processo-administrativo/resource/controlecnhpas/new', movimento).then(function(data) {
            
            if(data.listSelectItem){ 
                $scope.listas = data.listSelectItem; 
            }
            
            $scope.data = data.objectResponse;
            $scope.data.entidade.cnhControle = args.obj.entidade.cnhControle;
            
            $scope.dataModal.postoDescricaoDesbloquear = $scope.data.entidade.postoAtendimento.descricao;
            
            $scope.$broadcast('executeAfterNew' + 'controlecnhpaConfig', data.entity);
            $scope.$broadcast('executeAfterNewWithReturn' + 'controlecnhpaConfig', data);
        });
        
        $scope.filesPath = function(file) {

            return '/detran-processo-administrativo/site/modulos/adm/controlecnhpa/' + file + "";
        };
        
        $scope.adicionarResponsavel = function(){
            
            var modalResponsavelConfiguration = {
                tplUrl: '/detran-processo-administrativo/site/modulos/adm/responsavelprot/representantelegal.html',
                modalCtrl: 'responsavelProtocoloCtrl',
                name: 'responsavelProtocolo',
                args: {
                    config:args.config,
                    windowClass: "app-modal-window-md"
                }
            };

            if (!$scope.modalIsLoading) {
                dtnModal.modalForm(modalResponsavelConfiguration, $scope);
            }

            $scope.$on('modalOkresponsavelProtocolo', function (event, retorno) {
                $scope.data.representanteLegal = retorno;
            });
        };
        
        $scope.limparResponsavel = function(){  
            delete($scope.data.representanteLegal);
        };
        
        $scope.typePosto = ['posto'];
        $scope.postoModalDesbloquearCnh = function (type) {
            
            if (type == 'posto') {
                if ($scope.dataModal.postoDescricaoDesbloquear == undefined 
                        || ($scope.dataModal.postoDescricaoDesbloquear.length < 5 && $scope.dataModal.postoDescricaoDesbloquear || $scope.dataModal.postoDescricaoDesbloquear == '')) {
                    showMsgWarn(growl, "É necessário digitar no minimo 5 (cinco) caracteres.");
                    return;
                }
            };

            var modalConfiguration = {
                name: "PostoModal",
                resourcePath: 'postoatendimentos/search',
                params: {
                    descricao: $scope.dataModal.postoDescricaoDesbloquear,
                    populateList: false
                },
                inputModel: 'dataModal.postoDescricaoDesbloquear',
                inputHidden: 'data.entidade.postoAtendimento',
                args: {
                    "type": "table",
                    "model": [
                        {field: 'entidade.id', hidden: "hide"},
                        {field: 'entidade.versaoRegistro', hidden: "hide"},
                        {field: 'entidade.descricao', displayName: "Descrição"},
                    ],
                    "title": 'Posto de Atendimento'
                },
                fieldDisplayName: 'descricao',
                config: args.config
            };

            if (!$scope.modalIsLoading) {
                dtnModal.getModal(modalConfiguration, $scope);
            }
        };

        $scope.$on("selectedModalItemPostoModal", function (event, data) {

            if (angular.isUndefined(data))
                return;

            if (!angular.isUndefined(data.entidade)) {
                $scope.data.entidade.postoAtendimento = {
                            id: data.entidade.id,
                            versaoRegistro: data.entidade.versaoRegistro
                };
                $scope.dataModal.postoDescricaoDesbloquear = data.entidade.descricao;
            } else {
                $scope.data.entidade.postoAtendimento = {
                            id: data.id,
                            versaoRegistro: data.versaoRegistro
                };
                $scope.dataModal.postoDescricaoDesbloquear = data.descricao;
            }
        });

        $scope.$watch('dataModal.postoDescricaoDesbloquear', function (newValue, oldValue) {
            if ((newValue != oldValue) && newValue == '' && $scope.data.entidade != undefined) {
                 $scope.data.entidade.postoAtendimento = {};
            }
        });
        
        $scope.validateForm = function(){
            return detranUtil.ehBrancoOuNulo($scope.data)
                    || detranUtil.ehBrancoOuNulo($scope.data.entidade.templateProtocolo)
                    || detranUtil.ehBrancoOuNulo($scope.data.entidade.templateProtocolo.formaProtocolo)
                    || detranUtil.ehBrancoOuNulo($scope.data.entidade.templateProtocolo.responsavelProtocolo)
                    || ($scope.data.entidade.templateProtocolo.responsavelProtocolo == 'REPRESENTANTE_LEGAL' && detranUtil.ehBrancoOuNulo($scope.data.representanteLegal)
                    || $scope.data.entidade.postoAtendimento == null);
        };
        
        $scope.ok = function(){
            srvRest.gravar('detran-processo-administrativo/resource/controlecnhpas/desbloquear', $scope.data, 'btnSaveUpdate').then(function (retorno) {
                
                if (!_.isEmpty(retorno.error)) {
                    detranUtil.showMsg(growl,retorno.error);
                } else {
                    showMsgSuccess(growl,retorno.message);
                    $modalInstance.dismiss();
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        };
        
        $scope.cancel = function(){
            $modalInstance.dismiss();
        };
    }
]);