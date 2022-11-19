angular.module('recurso', ['ngRoute'])

.constant("recursoConfig", {
    name: 'recurso',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/recursos/search',
        "gravar": 'detran-processo-administrativo/resource/recursos/save',
        "novo": 'detran-processo-administrativo/resource/recursos/new',
        "buscarProcessosAdministrativos": 'detran-processo-administrativo/resource/recursos/buscarProcessosAdministrativos',
        "exportarprotocolo": 'detran-processo-administrativo/resource/recursos/exportarprotocolo'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "versaoRegistro", hidden: "hide"},
        {field: "numeroDocumento", displayName: "recurso.label.cpf"}, 
        {field: "entidade.processoAdministrativo.numeroProcessoMascarado", displayName: "recurso.label.numeroProcesso"}, 
        {field: "protocolo.numeroProtocoloMascarado", displayName: "recurso.label.numeroProtocolo"}, 
        {field: "dataRecurso", displayName: "recurso.label.dataRecurso"}, 
        {field: "tipoLabel", displayName: "recurso.label.tipo"},
        {field: "entidade.destinoFase.origemDestino", displayName: "recurso.label.origemDestino"},
        {field: "resultadoLabel", displayName: "recurso.label.resultado"}, 
        {field: "dataResultado", displayName: "recurso.label.dataResultado"},
        {field: "situacaoLabel", displayName: "recurso.label.situacao"},
        {field: "ativoLabel", displayName: "modulo.global.label.ativoLabel", hidden: "hide"},
        {field: "actions", actions: [
            {name: "apresentacao", icon: "file-pdf-o", btn: "info", title: "Protocolo Apresentação", typeicon: "fa"},
            {name: "cancelamento", icon: "file-pdf-o", btn: "warning", title: "Protocolo Cancelamento", typeicon: "fa"},
            {name: "cancelar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "Cancelar", nomePadrao: "CANCELAR RECURSO"}
        ]}
    ],
    filesPath :  '/detran-processo-administrativo/site/modulos/adm/recurso',
    urlPath : '/adm/recurso',
    i18nextResources: {
        "recurso": {
            "titulo": "Recurso",
            "label": {
                "cpf":"CPF",
                "numeroProcesso":"Nº Processo",
                "numeroProtocolo":"Nº Protocolo",
                "dataRecurso":"Data Recurso",
                "dataResultado":"Data Resultado",
                "dataInicioResultado":"Data Início Resultado",
                "dataFimResultado":"Data Fim Resultado",
                "usuarioResultado":"Relator",
                "resultado":"Resultado",
                "tipo": "Tipo",
                "situacao": "Situação",
                "foraPrazo": "Fora do Prazo",
                "conhecimento": "Conhecimento",
                "destino": "Destino",
                "motivoAlegacao": "Motivo Alegação",
                "forma": "Forma",
                "responsavel":"Responsável Protocolo",
                "representanteLegal":"Representante Legal",
                "observacao": "Observação",
                "origemDestino": "Destino",
                "dataInicioProtocolo": "Data Início Protocolo",
                "dataFimProtocolo": "Data Fim Protocolo"
            }
        }
    }
})

.config(["$routeProvider", "recursoConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'recurso.titulo'})
    .when( urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when( urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'})
    .when( urlPath + '/editar', {templateUrl: path + '/form.html', controller: name + 'Edit', label: 'global.label.breadcrumb.editar'});
}])

.controller('recursoCtrl', ["$scope", "$injector", "recursoConfig",
    function ($scope, $injector, config) {
        
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
        
        $scope.verificaPermissao = function(){
            
            $scope
                .carregar(
                    'detran-processo-administrativo/resource/usuarioacessopa/verificarpermissao',
                    {funcionalidadeURL: config.urlPath}).then(
                    function(retorno) {
                        
                        $scope.usuarioAcessoPA = retorno.objectResponse;
                        
                        $scope.data.indiceForaPrazo             = $scope.usuarioAcessoPA.foraPrazo;
                        $scope.data.dataRecurso                 = $scope.usuarioAcessoPA.dataRecurso;
                        $scope.data.desabilitadoDataRecurso     = $scope.usuarioAcessoPA.desabilitaDataRecurso;
                    }
                );
        };
    }
])

.controller('recursoList', ['$scope', '$controller', '$rootScope', 'growl', 'recursoConfig', 'utils', function($scope, $controller, $rootScope, growl, config, utils){
        $scope.page = 'list';
        $controller('recursoCustomCtrl', {$scope: $scope});
        $scope.verificaPermissao();
        if($rootScope.filtros && $rootScope.filtros.cpf){
            var cpf = $rootScope.filtros.cpf.replace(/\D/g,"");
            $scope.filtros.data.cpf = cpf;
            detranUtil.showMsgSuccess(growl,"Operação realizada com Sucesso");
            delete $rootScope.filtros.cpf;
            $scope.pesquisar();
        }else{
            $scope.load();
        }

        $scope.$on('afterLoadDataBasicCtrl' + config.name, function (event, obj) {
            $scope.total = $scope.pagination.totalItems;
        });


        /*ConsultaPessoa eh o my-name do componente. aposSelectedModalItem + my-name*/
        $scope.$on('aposSelectedModalItemConsultaPessoa', function () {
            if (typeof utils.getConsultaPessoa === "function") {
                if (angular.isDefined(utils.getConsultaPessoa())) {
    //                            $scope.cpfCnpj = utils.getConsultaPessoa().cpfCnpj;
    //                            console.log(utils.getConsultaPessoa());
                    if (_.isUndefined($scope.filtros.data))
                        $scope.filtros.data = {};
                    $scope.filtros.data.usuarioResultado = utils.getConsultaPessoa().nome;
                }
            }
        });



    $scope.$watch('dataModal.consultaPessoaNome', function (newValue, oldValue) {
        if ((newValue != oldValue)) {
            console.log("TESTE");
            }
    });

        $scope.$on('getCustomizeLine', function (event, objLine) {
            if($rootScope.usuario.master) return;
            var b = $(objLine.objHtml[0]).find("button");
           
            $(b).each(function(){
                if(this.name == 'cancelar'){
                    aplicaRegraPadraoFn(this); //regra padrão
                    $scope.$broadcast('validatePermissionSetEspecificActions', aplicarRegras);
                }
            });
        });
        var aplicarRegras = function(event, params){
           /*Aplicara a Regra específica(mostrar o botão cancelar) de visible caso tenha, senão aplicará a padrão (esconderá o botão cancelar) */
           var aplicaRegraEspecifica = params.item.visible && params.item.visible.toString()=="true";
           if(aplicaRegraEspecifica){ $(params.objItem.element[0]).css("display", "block"); }
        }, aplicaRegraPadraoFn = function(t){
                $(t).css("display", "none");
        };
        
        
        
       
        
    }
])

.controller('recursoCreate', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'create';
        $controller('recursoCustomCtrl', {$scope: $scope});
    }
])

.controller('recursoView', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'view';
        $controller('recursoCustomCtrl', {$scope: $scope});
    }
])

.controller('recursoEdit', ["$scope", "$controller", '$rootScope',
    function($scope, $controller, $rootScope){
        $scope.page = 'edit';
        $controller('recursoCustomCtrl', {$scope: $scope});
    }
])

.controller('recursoCustomCtrl', ["$scope", "$controller", "recursoConfig", "$rootScope", "$injector", "utils", "$filter", "growl", "detranModal", "dtnAttachFiles", "$location", "validatePermission",
    function($scope, ctrl, config, $rootScope, $injector, utils, $filter, growl, dtnModal, dtnAttachFiles, $location, validatePermission){
        ctrl('recursoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        /* Insere novos */
        var arrules = [{cancelar: {tooltip: "Cancelar", nomePadrao:"CANCELAR - REGISTRO"}}];
        validatePermission.setNewActionRules(arrules);
        
        $scope.bloquearListaPa = true;
        $scope.showList = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
            $scope.$broadcast('clearPessoaPorNomeCpfCnpjConsultaPessoa');
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.$on('getAction' + config.name, function (e, linha) {
            var wrapper = {};
            wrapper.entidade = {
                id: _.find(linha.data, {field: "id"}).value
            };
            
            if (linha.action === "apresentacao") {
                
                wrapper.tipoProtocolo = 'APRESENTACAO';
                exportar(wrapper);
                
            } else if (linha.action === "cancelamento") {
                
                wrapper.tipoProtocolo = 'CANCELAMENTO';
                exportar(wrapper);
                
            } else if (linha.action === "cancelar") {
                
                wrapper.entidade = {
                    id: _.find(linha.data, {field: "id"}).value
                };
                
                cancelar(wrapper);
            }
        });
        
        function cancelar(wrapper){
            var modalConfiguration = {
                tplUrl: config.filesPath + '/cancelar.html',
                modalCtrl: 'cancelarRecursoModalCtrl',
                name: 'cancelarrecurso',
                args: {
                    obj: wrapper,
                    windowClass: "app-modal-window-lg",
                    adicionarReponsavel: function() {
                        $scope.adicionarResponsavel();
                    }
                }
            };
            
            if (!$scope.modalIsLoading) {
                dtnModal.modalForm(modalConfiguration, $scope);
            }
            
            $scope.$on('modalCancelcancelarrecurso', function(event, retornoModal) {
                $scope.load();
            });
        }
        
        $scope.adicionarResponsavel = function() {
            var modalResponsavelConfiguration = {
                tplUrl: '/detran-processo-administrativo/site/modulos/adm/responsavelprot/representantelegal.html',
                modalCtrl: 'responsavelProtocoloCtrl',
                name: 'responsavelProtocolo',
                args: {
                    //obj: data,
                    config:config,
                    windowClass: "app-modal-window-lg"
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
        
        function exportar(wrapper) {
            $scope.editar(config.urls.exportarprotocolo, wrapper).then(function (retorno) {
                
                if (!_.isEmpty(retorno.error)) {
                    detranUtil.showMsg(growl,retorno.error);
                } else {
                    var byteArquivo = retorno.objectResponse.byteArquivo;
                    if (detranUtil.ehBrancoOuNulo(byteArquivo)) {
                        detranUtil.showMsg(growl, "Arquivo inválido.");
                        $rootScope.loading = false;
                        return
                    }
                    var item = { 
                        byteArquivo : byteArquivo,
                        extensao : "PDF",
                        usePdfJS : false,
                        nameModal : "Protocolo Apresentação",
                        orientation : 'landscape'
                    };
                    dtnAttachFiles().openFile(item, $scope);
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        }
        
        if ($scope.page === 'edit' || $scope.page === 'view') {
            $scope.bloquearListaPa = false;
            
            $scope.editar($scope.urls.pesquisar, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function(retorno){
                $scope.data = retorno.entity[0];
                $scope.data.entidade.processoAdministrativo = angular.fromJson($scope.data.entidade.processoAdministrativo);
            });
        }
        
        if ($scope.page === 'edit' || $scope.page === 'create') {
            
            $scope.verificaPermissao();
            
            $scope.validateForm = function() {
                
                if (!$scope.dataChanged()) 
                    return true;
                
                if (!angular.isUndefined($scope.data)) {
                    
                    if (!angular.isUndefined($scope.data.entidade)) {
                        
                        if (!detranUtil.ehBrancoOuNulo($scope.data.entidade.processoAdministrativo)
                                && !detranUtil.ehBrancoOuNulo($scope.data.indiceForaPrazo)
                                && !detranUtil.ehBrancoOuNulo($scope.data.destino)
                                && !detranUtil.ehBrancoOuNulo($scope.data.forma)
                                && !detranUtil.ehBrancoOuNulo($scope.data.responsavel)
                                && !detranUtil.ehBrancoOuNulo($scope.data.dataRecurso)) {

                            return false;
                        }
                    }
                }
                
                return true;
            };
            
            $scope.preSave = function() {
                
                var aux = angular.copy($scope.data.entidade.processoAdministrativo);
                
                $scope.data.entidade.processoAdministrativo = 
                        angular.fromJson($scope.data.entidade.processoAdministrativo).processoAdministrativo;
                
                $scope.save(false, false);
                
                $scope.data.entidade.processoAdministrativo = angular.copy(aux);
            };
            
            $scope.$on('savedData' + config.name, function(event, data, retorno){
                try{
                    $rootScope.filtros = $rootScope.filtros || {};
                    $rootScope.filtros.cpf = data.entidade.processoAdministrativo.cpf;
                }catch(e){
                    console.error(e);
                }
                $location.path(config.urlPath);
            });
        }
        
        $scope.populaDestino = function(){
            
            if (!angular.isUndefined($scope.data.entidade)
                && !angular.isUndefined($scope.data.entidade.processoAdministrativo) 
                && !ehBrancoOuNulo($scope.data.entidade.processoAdministrativo)) {
            
                var processoAdministrativoRecursoWrapper = angular.fromJson($scope.data.entidade.processoAdministrativo);

                if (!angular.isUndefined(processoAdministrativoRecursoWrapper) 
                        && !ehBrancoOuNulo(processoAdministrativoRecursoWrapper)
                        && !ehBrancoOuNulo(processoAdministrativoRecursoWrapper.origemDestino)) {
                    $scope.data.destino = processoAdministrativoRecursoWrapper.origemDestino;
                }
            }
        };
        
        $scope.buscarProcessosAdministrativos = function() {
            
            $scope.data.entidade = $scope.data.entidade || {};
            $scope.data.entidade.processoAdministrativo = "";
            $scope.data.destino = "";
            
            if (!angular.isUndefined($scope.data.numeroDocumento) && !ehBrancoOuNulo($scope.data.numeroDocumento)) {
                
                $scope.editar($scope.urls.buscarProcessosAdministrativos, $scope.data.numeroDocumento).then(function(retorno) {
                    $scope.processosAdministrativosSelect = [];
                    $scope.bloquearListaPa = true;

                    if (!_.isEmpty(retorno.error)) {
                        detranUtil.showMsg(growl, retorno.error);

                    } else {

                        $scope.data.entidade = $scope.data.entidade || {};
                        $scope.data.entidade.processoAdministrativo = "";

                        var collection = [
                            {
                                item:'processoAdministrativo', 
                                fn: function(item){ 
                                    $scope.data.entidade.processoAdministrativo = item; }, 
                                listName: 'processosAdministrativos'
                            },
                            {
                                item:'indiceForaPrazo', 
                                fn: function(item){$scope.data.indiceForaPrazo = item; }
                            },
                        ];

                        detranUtil.setValuesToCombos(retorno.listSelectItem, collection, $scope.data.entidade);
                        $scope.bloquearListaPa = false;
                    }
                });
            }
        };
        
        $scope.$on('newClicked' + config.name, function (event) {
            $scope.verificaPermissao();
        });
}])
    
.controller('cancelarRecursoModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", "args", "$injector", "detranModal",
    function($scope, $modalInstance, growl, srvRest, args, $injector, dtnModal) {
        
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.page = 'create';
        $scope.data = {};
        
        srvRest.editar('detran-processo-administrativo/resource/recursos/new', '{}').then(function(data) {
            if(data.listSelectItem){ $scope.listas = data.listSelectItem; }
            $scope.$broadcast('executeAfterNew' + 'recursoConfig', data.entity);
            $scope.$broadcast('executeAfterNewWithReturn' + 'recursoConfig', data);
        });
        
        $scope.filesPath = function(file) {

            return '/detran-processo-administrativo/site/modulos/adm/recurso/' + file + "";
        };
        
        $scope.adicionarResponsavel = function(){
            
            var modalResponsavelConfiguration = {
                    tplUrl: '/detran-processo-administrativo/site/modulos/adm/responsavelprot/representantelegal.html',
                    modalCtrl: 'responsavelProtocoloCtrl',
                    name: 'responsavelProtocolo',
                    args: {
    //                    obj: data,
                        config:args.config,
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
            delete($scope.data.representanteLegal);
        };
        
        $scope.validateForm = function(){
            return detranUtil.ehBrancoOuNulo($scope.data)
                    || detranUtil.ehBrancoOuNulo($scope.data.forma)
                    || detranUtil.ehBrancoOuNulo($scope.data.responsavel)
                    || ($scope.data.responsavel == 'REPRESENTANTE_LEGAL' && detranUtil.ehBrancoOuNulo($scope.data.representanteLegal));
        };
        
        $scope.ok = function(){
            $scope.data.entidade = {
                id: args.obj.entidade.id
            };
            
            srvRest.gravar('detran-processo-administrativo/resource/recursos/cancelar', $scope.data, 'btnSaveUpdate').then(function (retorno) {
                
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
