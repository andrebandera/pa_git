angular.module('movimentacao', ['ngRoute'])

        .constant("movimentacaoConfig", {
            name: 'movimentacao',
            urls: {
                "buscarProcessoAdministrativo": 'detran-processo-administrativo/resource/pa/movimentacaopa/buscarProcessoAdministrativo',
                "pesquisar": 'detran-processo-administrativo/resource/pa/movimentacaopa/search',
                "novo": 'detran-processo-administrativo/resource/pa/movimentacaopa/new',
                "gravar": 'detran-processo-administrativo/resource/pa/movimentacaopa/save'
            },
            model: [],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/movimentacao',
            urlPath: '/adm/movimentacao',
            i18nextResources: {
                "movimentacao": {"titulo": "Movimentar Processo Administrativo",
                    "label": {
                        numeroProcesso: "Número Processo",
                        nomeCondutor: "Nome Condutor",
                        cpf: "CPF",
                        tipoProcesso: "Tipo Processo",
                        dataProcessamento: "Data Processamento",
                        andamentoAtual: "Andamento Atual",
                        acao: "Ação",
                        motivo: "Motivo",
                        observacao: "Observação"
                    }
                }
            }
        })

        .config(["$routeProvider", "movimentacaoConfig", function ($rp, config) {
                var path = config.filesPath;
                var urlPath = config.urlPath;
                var name = config.name;

                $rp
                        .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'Create', label: 'movimentacao.titulo'})
                        .when(urlPath + '/novo', {templateUrl: path + '/index.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'});
            }])

        .controller('movimentacaoCtrl', ["$scope", "$injector", "movimentacaoConfig",
            function ($scope, $injector, config) {
                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            }
        ])

        .controller('movimentacaoCreate', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'create';
                $controller('movimentacaoCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('movimentacaoEdit', ["$scope", "$controller", '$rootScope',
            function ($scope, $controller, $rootScope) {
                $scope.page = 'edit';
                $controller('movimentacaoCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('movimentacaoCustomCtrl', ["$scope", "$controller", "movimentacaoConfig", "$rootScope", "$injector", "utils", "$filter", "growl", "detranModal", "dtnAttachFiles",
            function ($scope, ctrl, config, $rootScope, $injector, utils, $filter, growl, dtnModal, dtnAttachFiles) {
                ctrl('movimentacaoCtrl', {$scope: $scope});
                $injector.invoke(selectCtrl, this, {$scope: $scope});
                
                var numeroProcesso = "",busca = false;
                
                $scope.limparMotivo = function () {
                    $scope.data.entidade.motivo = null;
                }

                $scope.limpar = function () {
                    if (angular.isDefined($scope.data)
                    && angular.isDefined($scope.data.processoAdministrativo) && $scope.data.processoAdministrativo.numeroProcessoMascarado != numeroProcesso && busca) {
                        $scope.data.nomeCondutor = null;
                        $scope.data.andamentoAtual = null;
                        $scope.data.processoAdministrativo.cpf = null;
                        $scope.data.processoAdministrativo.tipo = null;
                        $scope.data.processoAdministrativo.dataProcessamento = null;
                        $scope.data.entidade.observacao = null;
                        $scope.data.entidade.motivo = null;
                        $scope.data.entidade.movimentacaoAcao = null;
                        busca = false;
                    }
                }

                $scope.buscarProcessoAdministrativo = function () {
                    if (angular.isDefined($scope.data.processoAdministrativo)
                            && angular.isDefined($scope.data.processoAdministrativo.numeroProcessoMascarado) && !detranUtil.ehBrancoOuNulo($scope.data.processoAdministrativo.numeroProcessoMascarado)) {

                        $scope.editar($scope.urls.buscarProcessoAdministrativo, $scope.data.processoAdministrativo.numeroProcessoMascarado).then(function (retorno) {
                            if (!_.isEmpty(retorno.error)) {
                                var numeroProcessoMascarado = angular.copy($scope.data.processoAdministrativo.numeroProcessoMascarado);
                                $scope.data.nomeCondutor = null;
                                $scope.data.andamentoAtual = null;
                                $scope.data.processoAdministrativo = null;
                                $scope.data.processoAdministrativo = {
                                    numeroProcessoMascarado: numeroProcessoMascarado
                                };
                                detranUtil.showMsg(growl, retorno.error);
                            } else {
                                numeroProcesso = $scope.data.processoAdministrativo.numeroProcessoMascarado;
                                busca = true;
                                $scope.data = retorno.objectResponse;
                            }
                        });
                    }
                };

                $scope.reativarInfracao = function (item, reativar) {
                    item.reativarPontuacao = reativar;
                };

                $scope.validateForm = function () {
                    return detranUtil.ehBrancoOuNulo($scope.data)
                            || detranUtil.ehBrancoOuNulo($scope.data.processoAdministrativo)
                            || detranUtil.ehBrancoOuNulo($scope.data.processoAdministrativo.numeroProcessoMascarado)
                            || detranUtil.ehBrancoOuNulo($scope.data.entidade)
                            || detranUtil.ehBrancoOuNulo($scope.data.entidade.movimentacaoAcao)
                            || detranUtil.ehBrancoOuNulo($scope.data.entidade.motivo)
                            || detranUtil.ehBrancoOuNulo($scope.data.entidade.observacao);
                };

                $scope.preSave = function () {
                    $scope.killAction = true;
                    var modalConfiguration = {
                        tplUrl: config.filesPath + '/confirmar.html',
                        modalCtrl: 'opcaoConfirmarModalCtrl',
                        name: 'confirmar',
                        args: {
                            obj: $scope.data,
                            windowClass: "app-modal-window-lg"
                        }
                    };

                    if (!$scope.modalIsLoading) {
                        dtnModal.modalForm(modalConfiguration, $scope);
                    }

                    $scope.$on('modalOkconfirmar', function (event, retornoModal) {
                        $scope.limpar();
                        $scope.load();
                        $scope.new();
                    });
                };
            }])

        .controller('opcaoConfirmarModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", "args",
            function ($scope, $modalInstance, growl, srvRest, args) {

                $scope.data = args.obj;

                $scope.data.entidade.nomeComponente = 'btnSave'

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function () {
                    srvRest.gravar('detran-processo-administrativo/resource/pa/movimentacaopa/save', $scope.data).then(function (retorno) {
                        if (retorno.error != "") {
                            showMsg(growl, retorno.error);
                        } else {
                            showMsgSuccess(growl, retorno.message);
                            $modalInstance.close(retorno);
                        }
                    }, function (err) {
                        showMsg(growl, err.error);
                    });
                };
            }
        ]);   