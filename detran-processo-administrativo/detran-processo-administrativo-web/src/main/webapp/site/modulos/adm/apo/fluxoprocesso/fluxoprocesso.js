/* 
 * @author: Carlos Eduardo
 * 
 */
angular.module('fluxoprocesso', ['ngRoute'])

.constant("fluxoprocessoConfig", {
    name: 'fluxoprocesso',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pafluxoprocessos/search',
        "gravar": 'detran-processo-administrativo/resource/pafluxoprocessos/save',
        "desativar": 'detran-processo-administrativo/resource/pafluxoprocessos/',
        "reativar": 'detran-processo-administrativo/resource/pafluxoprocessos/reativar',
        "novo": 'detran-processo-administrativo/resource/pafluxoprocessos/new',
        "editar": 'detran-processo-administrativo/resource/pafluxoprocessos/editar',
        "buscarFluxoProcessoeAmparoLegal": 'detran-processo-administrativo/resource/pafluxoprocessos/buscarFluxoProcessoeAmparoLegal'
       },
    model: [
        {field: "entidade.id", hidden: "hide"},
        {field: "entidade.versaoRegistro", hidden: "hide"},
        {field: "entidade.codigo", displayName: "fluxoprocesso.label.codigo"},
        {field: "entidade.descricao", displayName: "fluxoprocesso.label.descricao"},
        {field: "fluxoIndependenteLabel", displayName: "fluxoprocesso.label.fluxoIndependente"},
        {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
            {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
            {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
            {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso',
    urlPath: '/adm/apo/fluxoprocesso',
    i18nextResources: {
        "fluxoprocesso": {
            "titulo": "Fluxo do Processo",
            "label": {
                "codigo": "Código Fluxo",
                "descricao": "Descrição Fluxo",
                "amparoLegal": "Amparo Legal",
                "fluxoIndependente": "Fluxo Independente",
                "codigoAndamento": "Código Andamento",
                "descricaoAndamento": "Descrição Andamento"
            },
            "amparoLegal" : {
                "selecionar" : "Selecionar Amparo Legal",
                "artigo" : "Artigo",
                "inciso" : "inciso",
                "paragrafo" : "Parágrafo",
                "documentoRegulamentador" : "Documento Regulamentador"
            }
        }
    }
})

//This configures the routes and associates each route with a view and a controller
.config(["$routeProvider", "fluxoprocessoConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;

    $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'fluxoprocesso.titulo'})
    .when(urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when(urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'})
    .when(urlPath + '/editar', {templateUrl: path + '/form.html', controller: name + 'Edit', label: 'global.label.breadcrumb.editar'});
}])

.controller('fluxoprocessoCtrl', ["$scope", "$injector", "fluxoprocessoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('fluxoprocessoList', ['$scope', '$controller', function ($scope, $controller) {
        $scope.page = 'list';
        $controller('fluxoprocessoCustomCtrl', {$scope: $scope});

        //Carregar dados
        $scope.load();
    }
])

.controller('fluxoprocessoCreate', ['$scope', '$controller', function ($scope, $controller) {
        $scope.page = 'create';
        $controller('fluxoprocessoCustomCtrl', {$scope: $scope});
    }
])

.controller('fluxoprocessoView', ['$scope', '$controller', function ($scope, $controller) {
        $scope.page = 'view';
        $controller('fluxoprocessoCustomCtrl', {$scope: $scope});
    }
])

.controller('fluxoprocessoEdit', ["$scope", "$controller", '$rootScope',
    function ($scope, $controller, $rootScope) {
        $scope.page = 'edit';
        $controller('fluxoprocessoCustomCtrl', {$scope: $scope});
    }
])

.controller('fluxoprocessoCustomCtrl', ["$scope", "$controller", "fluxoprocessoConfig", "$rootScope", "$injector", "$location", "detranModal", "growl", "utils","srvMestreDtl",
    function ($scope, ctrl, config, $rootScope, $injector, $location, dtnModal, growl, utils, srvMestreDtl) {
        ctrl('fluxoprocessoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.dataModal = {};

        var setAmparoLegal = function(amparoLegal){
            $scope.amparoLegal = [];
            var amparoLegal = amparoLegal != null ? amparoLegal : {};
            amparoLegal.tipoDocumento = 
                    !detranUtil.ehBrancoOuNulo(amparoLegal.documentoRegulamentador) ? (amparoLegal.documentoRegulamentador.tipoDocumento || "")
                        : detranUtil.ehBrancoOuNulo(amparoLegal.tipoDocumento) ? "" : amparoLegal.tipoDocumento || "";
            amparoLegal.artigo = detranUtil.ehBrancoOuNulo(amparoLegal.artigo) ? "" : amparoLegal.artigo;
            amparoLegal.inciso = detranUtil.ehBrancoOuNulo(amparoLegal.inciso) ? "" : amparoLegal.inciso;
            amparoLegal.paragrafo = detranUtil.ehBrancoOuNulo(amparoLegal.paragrafo) ? "" : amparoLegal.paragrafo;

            $scope.amparoLegal =  [
                {label: utils.fnTranslateI18n('fluxoprocesso.amparoLegal.documentoRegulamentador'), value: amparoLegal.tipoDocumento, cols: "12 col-md-3"},
                {label: utils.fnTranslateI18n('fluxoprocesso.amparoLegal.artigo'), value: amparoLegal.artigo, cols: "12 col-md-3"},
                {label: utils.fnTranslateI18n('fluxoprocesso.amparoLegal.inciso'), value: amparoLegal.inciso, cols: "12 col-md-3"},
                {label: utils.fnTranslateI18n('fluxoprocesso.amparoLegal.paragrafo'), value: amparoLegal.paragrafo, cols: "12 col-md-3"}
            ];
        };
        
        if ($scope.page === 'view' || $scope.page === 'edit') {
            $scope.showTabs = true;
            $scope.amparoLegal = "";
            
            if (!angular.isUndefined($rootScope.idPaginaAtual)) {
                
                var criteria = {id : $rootScope.idPaginaAtual };
                
                $scope.carregar(config.urls.buscarFluxoProcessoeAmparoLegal, criteria).then(function (retorno) {
                    if (retorno.error != "") {
                        detranUtil.showMsg(growl, retorno.error);
                    } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                        detranUtil.showMsgWarn(growl, retorno.warning);
                    } else {
                        if (retorno.entity && retorno.entity[0]) {
                            $scope.data = retorno.entity[0];
                            $scope.dataModal = {};
                            setAmparoLegal($scope.data.amparoLegal);
                            $rootScope.mestre.fluxoProcesso = srvMestreDtl.setMestre('fluxoProcesso', $scope.data.entidade);
                        }
                    }
                });
            }
        }

        if ($scope.page === 'edit' || $scope.page === 'create') {
                $scope.amparoLegalModal = function () {
                var modalConfiguration = {
                    name: "AmparoLegalModal",
                    resourcePath: 'amparolegals/searchAmparoLegalWrapper',
                    params: {},
                    inputModel: 'dataModal.amparoLegal',
                    inputHidden: 'data.amparoLegal',
                    args: {
                        "type": "table",
                        "model": [
                            {field: 'entidade.documentoRegulamentador.tipoDocumento',
                                displayName: "Documento Regulamentar"},
                            {field: 'entidade.id', hidden: "hide"},
                            {field: 'entidade.artigo', displayName: "Artigo"},
                            {field: 'entidade.paragrafo', displayName: "Parágrafo"},
                            {field: 'entidade.inciso', displayName: "Inciso"}
                            // {field: 'entidade.referencia', displayName: "Referência"},

                        ],
                        "title": 'Amparo Legal'
                    },
                    config: config
                };

                if (!$scope.modalIsLoading) {
                    dtnModal.getModal(modalConfiguration, $scope);
                }
            };

            $scope.$on('selectedModalItemAmparoLegalModal', function (event, data) {
                if (angular.isUndefined(data))
                    return;
                setAmparoLegal(data);
            });

            $scope.$watch('dataModal.amparoLegal', function (newValue, oldValue) {
                if ((newValue != oldValue) && newValue == '' && $scope.data.entidade != undefined) {
                    $scope.data.amparoLegal = {};
                    $scope.dataModal.amparoLegal = '';
                    $scope.amparoLegal = [];
                }
            });

            $scope.validateForm = function () {
                if (!$scope.dataChanged())
                    return true;

                if (angular.isDefined($scope.data.entidade)) {
                    if ($scope.data.entidade == undefined)
                        return true;
                    
                    if ($scope.data.amparoLegal
                            && $scope.data.entidade.descricao
                            && $scope.data.entidade.fluxoIndependente)
                        return false;
                }
                
                return true;
            };

            $scope.preSave = function () {
                $scope.data.entidade.amparoLegal = $scope.data.amparoLegal.id;
                delete($scope.data.amparoLegal.documentoRegulamentador);
                $scope.save();
            };
        }
        
        $scope.searchAndamentoPorDescricao = function() {

            var configModal = angular.copy(config);

            configModal.itemsPerPage = 10;

            var modalConfiguration = {
                name: 'Andamento',
                resourcePath: '/detran-processo-administrativo/resource/paandamentoprocessos/searchAndamentoPorDescricao',
                params: {
                    descricao: $scope.filtros.data.descricaoAndamento
                },
                inputModel: 'filtros.data.descricaoAndamento',
                args: {
                    "type": "table",
                    "model": [
                        {field: 'entidade.codigo', displayName: "fluxoprocesso.label.codigoAndamento"},
                        {field: 'entidade.descricao', displayName: "fluxoprocesso.label.descricaoAndamento"}
                    ],
                    "title": 'Andamento'
                },
                fieldDisplayName: 'entidade.descricao',
                config: configModal
            };

            if (!$scope.modalIsLoading) {
                dtnModal.getModal(modalConfiguration, $scope);
            }
        };
        
        $scope.$on("selectedModalItemAndamento", function (event, data) {
            if (data && data.codigo && data.descricao) {
                $scope.filtros.data.codigoAndamento = data.codigo;
                $scope.filtros.data.descricaoAndamento = data.descricao;
            } else {
                $scope.filtros.data.codigoAndamento = {};
                $scope.filtros.data.descricaoAndamento = {};
            }
        });
        
        $scope.$watch('filtros.data.descricaoAndamento', function(newValue, oldValue){
            if ((newValue !== oldValue) && newValue === '') {
                $scope.filtros.data.descricaoAndamento = null;
            }
        });
}]);