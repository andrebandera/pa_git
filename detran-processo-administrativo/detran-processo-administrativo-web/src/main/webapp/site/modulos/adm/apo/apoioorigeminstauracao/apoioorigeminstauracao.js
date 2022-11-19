/* 
 * @author: Carlos Eduardo
 * 
 */

//Create module
angular.module('apoioorigeminstauracao', ['ngRoute'])

        .constant("apoioorigeminstauracaoConfig", {
            name: 'apoioorigeminstauracao',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/search',
                "gravar": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/save',
                "desativar": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/',
                "reativar": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/reativar',
                "novo": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/new',
                "editar": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/editar',
                "buscarApoioOrigemeAmparoLegal": 'detran-processo-administrativo/resource/apoioorigeminstauracaos/buscarApoioOrigemeAmparoLegal'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.regra", displayName: "apoioorigeminstauracao.label.regra"},
                {field: "entidade.descricao", displayName: "apoioorigeminstauracao.label.descricao"},
                {field: "resultadoTipoProcessoLabel", displayName: "apoioorigeminstauracao.label.tipoProcesso"},
                {field: "resultadoMotivoLabel", displayName: "apoioorigeminstauracao.label.motivo"},
                {field: "resultadoAcaoLabel", displayName: "apoioorigeminstauracao.label.acao"},
                {field: "acaoSistemaLabel", displayName: "apoioorigeminstauracao.label.acaoSistema"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/apoioorigeminstauracao',
            urlPath: '/adm/apo/apoioorigeminstauracao',
            i18nextResources: {
                "apoioorigeminstauracao": {
                    "titulo": "Apoio Origem Instauracao",
                    "label": {

                        //Regra, Descrição, Tipo Processo Resultado, Motivo Resultado, Ação Resultado, Ação Sistema e Ativo, 
                        "regra": "Regra",
                        "descricao": "Descrição",
                        "artigoInciso": "Artigo e Inciso",
                        "motivo": "Motivo",
                        "tipoProcessoReincidencia": "Tipo Processo Reincidência",
                        "indiceHistoricoInfracao": "Indicativo Histórico Infração",
                        "tipoProcesso": "Tipo Processo Resultado",
                        "motivoResultado": "Motivo Resultado",
                        "acao": "Ação Resultado",
                        "reincidencia": "Reincidência",
                        "prioridade": "Prioridade",
                        "acaoSistema": "Ação Sistema",
                        "statusInfracao": "Status Infracao",
                        "origemInformacaoPontuacao": "Origem Informação Pontuação",
                        "indiceReincidenciaMAZ": "Indicativo Reincidência MAZ",
                        "codigoConfirmacaoMainframe": "Confirmação Mainframe",
                        "amparoLegal": "Amparo Legal"
                    },
                    "amparoLegal": {
                        "selecionar": "Selecionar Amparo Legal",
                        "artigo": "Artigo",
                        "inciso": "inciso",
                        "paragrafo": "Parágrafo",
                        "documentoRegulamentador": "Documento Regulamentador"
                    }
                }
            }
        })

//This configures the routes and associates each route with a view and a controller
        .config(["$routeProvider", "apoioorigeminstauracaoConfig", function ($rp, config) {
                var path = config.filesPath;
                var urlPath = config.urlPath;
                var name = config.name;

                $rp
                        .when(urlPath,
                                {
                                    templateUrl: path + '/index.html',
                                    controller: name + 'List',
                                    label: 'apoioorigeminstauracao.titulo'
                                })
                        .when(urlPath + '/novo',
                                {
                                    templateUrl: path + '/form.html',
                                    controller: name + 'Create',
                                    label: 'global.label.breadcrumb.novo'
                                })
                        .when(urlPath + '/ver',
                                {
                                    templateUrl: path + '/form.html',
                                    controller: name + 'View',
                                    label: 'global.label.breadcrumb.ver'
                                })
                        .when(urlPath + '/editar',
                                {
                                    templateUrl: path + '/form.html',
                                    controller: name + 'Edit',
                                    label: 'global.label.breadcrumb.editar'
                                });
            }])

//Este é o controller Principal. 
        .controller('apoioorigeminstauracaoCtrl', ["$scope", "$injector", "apoioorigeminstauracaoConfig",
            function ($scope, $injector, config) {
                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            }
        ])

//Listar conteúdos
        .controller('apoioorigeminstauracaoList', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'list';
                $controller('apoioorigeminstauracaoCustomCtrl', {$scope: $scope});

                //Carregar dados
                $scope.load();
            }
        ])

        .controller('apoioorigeminstauracaoCreate', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'create';
                $controller('apoioorigeminstauracaoCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('apoioorigeminstauracaoView', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'view';
                $controller('apoioorigeminstauracaoCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('apoioorigeminstauracaoEdit', ["$scope", "$controller", '$rootScope',
            function ($scope, $controller, $rootScope) {
                $scope.page = 'edit';
                $controller('apoioorigeminstauracaoCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('apoioorigeminstauracaoCustomCtrl', ["$scope", "$controller", "apoioorigeminstauracaoConfig", "$rootScope", "$injector", "$location", "detranModal", "srvMestreDtl", "growl", "utils",
            function ($scope, ctrl, config, $rootScope, $injector, $location, dtnModal, srvMestreDtl, growl, utils) {
                ctrl('apoioorigeminstauracaoCtrl', {$scope: $scope});
                $injector.invoke(selectCtrl, this, {$scope: $scope});


                $scope.showTabs = false;

                var setAmparoLegal = function (amparoLegal) {
                    $scope.amparoLegal = [];
                    var amparoLegal = amparoLegal != null ? amparoLegal : {};
                    amparoLegal.tipoDocumento = !detranUtil.ehBrancoOuNulo(amparoLegal.documentoRegulamentador) ? (amparoLegal.documentoRegulamentador.tipoDocumento || "")
                            : detranUtil.ehBrancoOuNulo(amparoLegal.tipoDocumento) ? "" : amparoLegal.tipoDocumento || "";
                    amparoLegal.artigo = detranUtil.ehBrancoOuNulo(amparoLegal.artigo) ? "" : amparoLegal.artigo;
                    amparoLegal.inciso = detranUtil.ehBrancoOuNulo(amparoLegal.inciso) ? "" : amparoLegal.inciso;
                    amparoLegal.paragrafo = detranUtil.ehBrancoOuNulo(amparoLegal.paragrafo) ? "" : amparoLegal.paragrafo;

                    $scope.amparoLegal = [
                        {label: utils.fnTranslateI18n('apoioorigeminstauracao.amparoLegal.documentoRegulamentador'), value: amparoLegal.tipoDocumento, cols: "12 col-md-3"},
                        {label: utils.fnTranslateI18n('apoioorigeminstauracao.amparoLegal.artigo'), value: amparoLegal.artigo, cols: "12 col-md-3"},
                        {label: utils.fnTranslateI18n('apoioorigeminstauracao.amparoLegal.inciso'), value: amparoLegal.inciso, cols: "12 col-md-3"},
                        {label: utils.fnTranslateI18n('apoioorigeminstauracao.amparoLegal.paragrafo'), value: amparoLegal.paragrafo, cols: "12 col-md-3"}

                    ];
                };


                //carregar dados apoioorigeminstauracao e amparo legal no modal.
                if ($scope.page === 'view' || $scope.page === 'edit') {
                    //Carregar dados
                    $scope.amparoLegal = "";
                    $scope.showTabs = true;
                    
                    if (!angular.isUndefined($rootScope.idPaginaAtual)) {
                        //var criteria = {id: $rootScope.idPaginaAtual};
                        $scope.carregar(config.urls.buscarApoioOrigemeAmparoLegal, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function (retorno) {
                            if (retorno.error != "") {
                                detranUtil.showMsg(growl, retorno.error);
                            } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                                detranUtil.showMsgWarn(growl, retorno.warning);
                            } else {
                                if (retorno.entity && retorno.entity[0]) {
                                    $scope.data = retorno.entity[0];
                                    $scope.dataModal = {};
                                    setAmparoLegal($scope.data.amparoLegal);
                                    $rootScope.mestre.origemInstauracao = srvMestreDtl.setMestre('origemInstauracao', $scope.data.entidade);
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
                        if ((newValue != oldValue) && newValue == '' 
                                && $scope.data.entidade != undefined) {
                            $scope.data.amparoLegal = {};
                            $scope.dataModal.amparoLegal = '';
                            $scope.amparoLegal = [];
                        }
                    });

                    ///// Faço a validaçao do meu formulario
                    $scope.validateForm = function () {
                        if (!$scope.dataChanged())
                            return true;

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isDefined($scope.data.entidade)) {
                            if ($scope.data.entidade == undefined)
                                return true;
                            if ($scope.data.amparoLegal
                                && $scope.data.entidade.descricao
                                && $scope.data.entidade.regra
                                && $scope.data.entidade.resultadoTipoProcesso
                                && $scope.data.entidade.resultadoMotivo
                                && $scope.data.entidade.resultadoAcao
                                && $scope.data.entidade.acaoSistema)
                                return false;
                        }
                        // bloqueia o botao gravar;
                        return true;
                    };

                    $scope.preSave = function () {
                        $scope.data.entidade.amparoLegal = $scope.data.amparoLegal.id;
                        delete($scope.data.amparoLegal.documentoRegulamentador);
                        $scope.save();
                    };
                }
            }]);