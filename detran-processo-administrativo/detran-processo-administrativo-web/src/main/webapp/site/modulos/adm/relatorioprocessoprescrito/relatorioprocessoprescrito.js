angular.module('relatorioprocessoprescrito', ['ngRoute'])

        .constant("relatorioprocessoprescritoConfig", {
            name: 'relatorioprocessoprescrito',
            label: 'Relatório Processos Prescritos',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/relatorioprocessoprescritos/search',
                "emitir": 'detran-processo-administrativo/resource/relatorioprocessoprescritos/emitir'
            },
            model: [
                {field: "id", hidden: "hide"},
                //{field: "dataInicio", displayName: "relatorioprocessoprescrito.label.dataInicialLabel"},
                //{field: "dataFinal", displayName: "relatorioprocessoprescrito.label.dataFinalLabel"},
                {field: "processoAdministrativo.numeroProcessoMascarado", displayName: "relatorioprocessoprescrito.label.numeroProcesso"},
                //{field: "infracaoProcessoAdministrativo.dataInfracao", displayName: "relatorioprocessoprescrito.label.dataInfracaoLabel"},
                {field: "processoAdministrativo.dataProcessamento", displayName: "relatorioprocessoprescrito.label.dataProcessamento"},
                //{field: "dataNotificacao", displayName: "relatorioprocessoprescrito.label.dataNotificacaoLabel"},
                {field: "fluxoFase.andamentoProcesso.descricao", displayName: "relatorioprocessoprescrito.label.descricaoLabel"},
                {field: "observacao", displayName: "relatorioprocessoprescrito.label.observacaoLabel"}
                //obs
                //numero processo
            ],
            btnFiltro: ['pesquisar', 'limpar'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/relatorioprocessoprescrito',
            urlPath: '/adm/relatorioprocessoprescrito',
            i18nextResources: {
                "relatorioprocessoprescrito": {
                    "titulo": "Relatório Processos Administrativos Prescritos",
                    "label": {
                        "dataInicialLabel": "Data Inicial",
                        "dataFinalLabel": "Data Final",
                        "numeroProcesso": "Número Processo",
                        "dataInfracaoLabel": "Data da Infração",
                        "dataProcessamento": "Data Processamento",
                        "dataNotificacaoLabel": "Data Notificação",
                        "descricaoLabel": "Descrição",
                        "observacaoLabel": "Observação"
                    }
                }
            }
        })

        .config(["$routeProvider", "relatorioprocessoprescritoConfig", function ($rp, config) {
                var path = config.filesPath;
                var urlPath = config.urlPath;
                var name = config.name;

                $rp
                        .when(urlPath,
                                {
                                    templateUrl: path + '/index.html',
                                    controller: name + 'List',
                                    label: 'relatorioprocessoprescrito.titulo'
                                });
            }])

        //Este é o controller Principal. 
        .controller('relatorioprocessoprescritoCtrl', ["$scope", "$injector", "relatorioprocessoprescritoConfig",
            function ($scope, $injector, config) {
                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            }
        ])

        //Listar conteúdos
        .controller('relatorioprocessoprescritoList', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'list';
                $controller('relatorioprocessoprescritoCustomCtrl', {$scope: $scope});
                $scope.habilitaImpressao = false;
                //Carregar dados
                //$scope.load(); //Mensagem: Favor preencher os campo de filtro!
            }
        ])

        .controller('relatorioprocessoprescritoCustomCtrl', ["$scope", "$controller", "relatorioprocessoprescritoConfig", "$rootScope", "$injector", "detranModal", "utils", "srvDetranAbstractResourceRest", '$sce', 'growl', "dtnAttachFiles",
            function ($scope, ctrl, config, $rootScope, $injector, dtnModal, utils, srvRest, $sce, growl, dtnAttachFiles) {
                
                ctrl('relatorioprocessoprescritoCtrl', {$scope: $scope});

                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                $scope.showViewer = false;
                $scope.desabilitaImpressao = true;

                // esconder a lista e mostrar so na pesquisa
                $scope.showList = false;

                // models
                // ======================================================
                $scope.printBtns = [
                    {name: 'PDF', color: 'red'}
                ];

                $scope.validarfiltros = function () {
                    if (_.isUndefined($scope.filtros.data))
                        return;
                    if ((angular.isUndefined($scope.filtros.data.dataInicial) && $scope.filtros.data.dataFinal == undefined)) {

                        return true;
                    }
                    return false;
                }

                $scope.$on('afterLoadDataBasicCtrl' + config.name, function (event, obj) {
                    $scope.total = $scope.pagination.totalItems;
                    if ($scope.dataForTable.length > 0)
                        $scope.desabilitaImpressao = false;
                    else
                        $scope.desabilitaImpressao = true;
                });


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
                                $scope.appFiles.openFile(item, $scope, false, "Relatório Processos Administrativos Prescritos");
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
