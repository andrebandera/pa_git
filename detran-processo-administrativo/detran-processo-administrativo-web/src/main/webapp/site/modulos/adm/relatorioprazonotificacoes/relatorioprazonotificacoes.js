angular.module('relatorioprazonotificacoes', ['ngRoute'])

        .constant("relatorioprazonotificacoesConfig", {
            name: 'relatorioprazonotificacoes',
            label: 'Relatório Prazo Notificações',
            urls: {
                "novo": 'detran-processo-administrativo/resource/relatorioprazonotificacoes/new',
                "pesquisar": 'detran-processo-administrativo/resource/relatorioprazonotificacoes/search',
                "emitir": 'detran-processo-administrativo/resource/relatorioprazonotificacoes/emitir'
            },
            model: [
                {field: "entidade.tipoBusca", displayName: "relatorioprazonotificacoes.label.tipoBuscaLabel"}
            ],
            btnFiltro: ['limpar'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/relatorioprazonotificacoes',
            urlPath: '/adm/relatorioprazonotificacoes',
            i18nextResources: {
                "relatorioprazonotificacoes": {
                    "titulo": "Relatório Prazo Notificações",
                    "label": {
                        "tipoBuscaLabel": "Buscar por"
                    }
                }
            }
        })

        .config(["$routeProvider", "relatorioprazonotificacoesConfig", function ($rp, config) {
                var path = config.filesPath;
                var urlPath = config.urlPath;
                var name = config.name;

                $rp
                        .when(urlPath,
                                {
                                    templateUrl: path + '/index.html',
                                    controller: name + 'List',
                                    label: 'relatorioprazonotificacoes.titulo'
                                });
            }])

        //Este é o controller Principal. 
        .controller('relatorioprazonotificacoesCtrl', ["$scope", "$injector", "relatorioprazonotificacoesConfig",
            function ($scope, $injector, config) {
                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                // models
                // ======================================================
                $scope.printBtns = [
                    {name: 'PDF', color: 'red'}
                ];
                $scope.desabilitaImpressao = false;
            }
        ])

        //Listar conteúdos
        .controller('relatorioprazonotificacoesList', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'list';
                $controller('relatorioprazonotificacoesCustomCtrl', {$scope: $scope});
//                $scope.habilitaImpressao = false;
                //Carregar dados
                $scope.load(); //Mensagem: Favor preencher os campo de filtro!
            }
        ])

        .controller('relatorioprazonotificacoesCustomCtrl', ["$scope", "$controller", "relatorioprazonotificacoesConfig", "$rootScope", "$injector", "detranModal", "utils", "srvDetranAbstractResourceRest", '$sce', 'growl', "dtnAttachFiles",
            function ($scope, ctrl, config, $rootScope, $injector, dtnModal, utils, srvRest, $sce, growl, dtnAttachFiles) {
                ctrl('relatorioprazonotificacoesCtrl', {$scope: $scope});

                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});

                $scope.emitir = function (type) {

                    // tipo do relatorio
                    $scope.filtros.data.formato = type;

                    srvRest.editar(config.urls.emitir, $scope.filtros.data).then(function (retorno) {
                        if (!_.isEmpty(retorno.error)) {
                            growl.addErrorMessage(retorno.error);
                        } else {
                            if (type == "PDF") {
                                $scope.appFiles = {};
                                $scope.appFiles = new dtnAttachFiles();
                                $scope.appFiles.setConfig($scope.config);
                                var item = {};
                                item.byteArquivo = retorno.objectResponse;
                                item.typeApplication = 'pdf';
                                $scope.appFiles.openFile(item, $scope, false, "Relatório Prazo Notificações");
                            } else {
                                var urlRelatorio = retorno.objectResponse;
                                $scope.showViewer = false;
                                window.open(getBaseUrlServer() + '/download?uid=' + urlRelatorio);
                            }
                        }
                    }, function (err) {
                        growl.addErrorMessage(err.error);
                    });
                };

                // to open external link in iframe
                // ==========================================
                $scope.trustSrc = function (src) {
                    return $sce.trustAsResourceUrl(src);
                };

            }]);
