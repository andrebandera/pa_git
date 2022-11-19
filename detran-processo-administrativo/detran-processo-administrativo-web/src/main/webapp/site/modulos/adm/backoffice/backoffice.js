angular
    .module('backoffice', ['ngRoute'])
    .constant("backofficeConfig", {
        name: 'backoffice',
        urls: {
            pesquisar: 'detran-processo-administrativo/resource/backoffices/search',
            gravar: 'detran-processo-administrativo/resource/backoffices/save',
            recusar: 'detran-processo-administrativo/resource/backoffices/recusar',
            editar: 'detran-processo-administrativo/resource/backoffices/editar',
            exportarprotocolo: 'detran-processo-administrativo/resource/recursos/exportarprotocolo'
        },
        model: [
            {field: "id", hidden: "hide"},
            {field: "entidade.recurso.id", hidden: "hide"},
            {field: "entidade.versaoRegistro", hidden: "hide"},
            {field: "entidade.ativo", hidden: "hide"},
            {field: "entidade.situacao", hidden: "hide"},
            {field: "entidade.numeroProcesso", displayName: "backoffice.label.numeroProcesso"},
            {field: "entidade.protocolo", displayName: "backoffice.label.protocolo"},
            {field: "tipoRecursoLabel", displayName: "backoffice.label.tipoRecurso"},
            {field: "entidade.destino", displayName: "backoffice.label.destinoRecurso"},
            {field: "entidade.cpf", displayName: "backoffice.label.cpf"},
            {field: "entidade.requerente", displayName: "backoffice.label.nome"},
            {field: "entidade.dataRecurso", displayName: "backoffice.label.dataRecurso"},
            {field: "situacaoLabel", displayName: "backoffice.label.situacao"},
            {
                field: "actions", actions: [
                    {
                        name: "ver",
                        icon: "global.btn.ver.icon",
                        btn: "global.btn.ver.btn",
                        title: "global.btn.ver.tooltip"
                    },
                    {
                        name: "editar",
                        icon: "global.btn.editar.icon",
                        btn: "global.btn.editar.btn",
                        title: "global.btn.editar.tooltip"
                    },
                    {
                        name: "gerarPdf",
                        icon: "file-pdf-o",
                        btn: "info",
                        title: "Gerar PDF Recurso Online",
                        typeicon: "fa"
                    }
//            {name: "cancelar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "Cancelar", nomePadrao: "CANCELAR"}
                ]
            }
        ],
        btnFiltro: ['pesquisar', 'limpar'],
        filesPath: '/detran-processo-administrativo/site/modulos/adm/backoffice',
        urlPath: '/adm/backoffice',
        i18nextResources: {
            backoffice: {
                titulo: "Back Office",
                label: {
                    efetivado: "Efetivado",
                    protocolo: "N. Protocolo",
                    numeroProcesso: "N. Processo",
                    dataNotificacao: "Data Notificação",
                    dataLimite: "Data Limite Recurso",
                    dataInicio: "Data Início",
                    dataFim: "Data Fim",
                    cpf: "CPF",
                    nome: "Nome",
                    dataRecurso: "Data Recurso",
                    rg: "RG",
                    endereco: "Endereço",
                    numero: "Número",
                    bairro: "Bairro",
                    uf: "UF",
                    cidade: "Cidade",
                    complemento: "Complemento",
                    cep: "CEP",
                    telefoneResidencial: "Telefone Residencial",
                    telefoneCelular: "Telefone Celular",
                    email: "E-mail",
                    descricao: "Descrição / Declaração / Alegação",
                    registroCnh: "Registro CNH",
                    situacao: "Situação",
                    motivoRecusa: "Descrição Motivo",
                    motivoCancelamento: "Descrição Motivo ",
                    tipoRecurso: "Tipo Recurso",
                    destinoRecurso: "Setor Destino",
                    tempestividade: "Tempestivo"
                }
            }
        }
    })

    .config(["$routeProvider", "backofficeConfig", function ($rp, config) {
        var path = config.filesPath;
        var urlPath = config.urlPath;
        var name = config.name;

        $rp
            .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'backoffice.titulo'})
            .when(urlPath + '/novo', {
                templateUrl: path + '/form.html',
                controller: name + 'Create',
                label: 'global.label.breadcrumb.novo'
            })
            .when(urlPath + '/ver', {
                templateUrl: path + '/form.html',
                controller: name + 'View',
                label: 'global.label.breadcrumb.ver'
            })
            .when(urlPath + '/editar', {
                templateUrl: path + '/form.html',
                controller: name + 'Edit',
                label: 'global.label.breadcrumb.editar'
            });
    }])

    .controller('backofficeCtrl', ["$scope", "$injector", "backofficeConfig",
        function ($scope, $injector, config) {
            $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
        }
    ])

    .controller('backofficeList', ['$scope', '$controller', '$rootScope', function ($scope, $controller, $rootScope) {

        $scope.page = 'list';
        $controller('backofficeCustomCtrl', {$scope: $scope});

        $scope.filtros.data.situacao = $scope.filtros.data.situacao || 'BACKOFFICE';
        $rootScope.filtros.situacao = 'BACKOFFICE';

        $scope.load();
    }
    ])

    .controller('backofficeCreate', ['$scope', '$controller', "hotkeys", function ($scope, $controller, hotkeys) {
        $scope.page = 'create';
        $controller('backofficeCustomCtrl', {$scope: $scope});
    }
    ])

    .controller('backofficeView', ['$scope', '$controller', function ($scope, $controller) {
        $scope.page = 'view';
        $controller('backofficeCustomCtrl', {$scope: $scope});
    }
    ])

    .controller('backofficeEdit', ["$scope", "$controller", function ($scope, $controller) {
        $scope.page = 'edit';
        $controller('backofficeCustomCtrl', {$scope: $scope});
    }
    ])

    .controller('backofficeCustomCtrl', ["$scope", "$controller", "backofficeConfig", "$rootScope",
        "utils", "srvDetranAbstractResourceRest", "growl", "dtnAttachFiles", "$location", "dialog", "detranModal",
        function ($scope, ctrl, config, $rootScope, utils, srvRest, growl, dtnAttachFiles, $location, dialog, dtnModal) {

            ctrl('backofficeCtrl', {$scope: $scope});

            $scope.showTabs = false;

            $scope.appFiles = {};
            $scope.appFiles.hideElements = {botaoAnexar: true, botoesExcluir: true, labelRestricoes: true};

            $scope.appFiles.attachFiles = new dtnAttachFiles();
            $scope.config.attachModalPath = ($rootScope.urlPartials || 'partials/') + 'modais/modalAnexos.html';
            $scope.appFiles.attachFiles.setImageViewer("gallery");
            $scope.appFiles.attachFiles.setLoading(true);

            $scope.config.urlRequestFile = detranUtil.getUrlServer() + "/consultararquivosanexadoss/searcharquivo";
            $scope.appFiles.attachFiles.setConfig($scope.config);

            $rootScope.filtros.ativo = $rootScope.filtros.ativo || 'ATIVO';
            $scope.filtros.data.ativo = $scope.filtros.data.ativo || 'ATIVO';

            $scope.$on("limparfiltros" + config.name, function () {
                $rootScope.filtros.ativo = 'ATIVO';
                $scope.filtros = {};
                $scope.filtros.data = {};
                $scope.filtros.data.ativo = 'ATIVO';
            });

            $scope.$on("getAction", function (itemname, linha) {

                var parametro = {
                    id: _.find(linha.data, {'field': 'id'}).value
                };

                if (linha.action === "gerarPdf") {

                    var recursoWrapper = {
                        entidade: {id: _.find(linha.data, {'field': 'entidade.recurso.id'}).value},
                        tipoProtocolo: "APRESENTACAO"
                    }
                    exportarRecurso(recursoWrapper);

                }

                if (linha.action === "cancelar") {
                    cancelar(parametro.id);
                }
            });

            function exportarRecurso(recursoWrapper) {
                $scope.editar(config.urls.exportarprotocolo, recursoWrapper).then(function (retorno) {

                    if (!_.isEmpty(retorno.error)) {
                        detranUtil.showMsg(growl, retorno.error);
                    } else {
                        var byteArquivo = retorno.objectResponse.byteArquivo;
                        if (detranUtil.ehBrancoOuNulo(byteArquivo)) {
                            detranUtil.showMsg(growl, "Arquivo inválido.");
                            $rootScope.loading = false;
                            return
                        }
                        var item = {
                            byteArquivo: byteArquivo,
                            extensao: "PDF",
                            usePdfJS: false,
                            nameModal: "Protocolo Apresentação",
                            orientation: 'landscape'
                        };
                        dtnAttachFiles().openFile(item, $scope);
                    }
                }, function (err) {
                    detranUtil.showMsg(growl, err.error);
                });
            }

            function cancelar(id) {

                var modalConfiguration = {
                    tplUrl: config.filesPath + '/cancelar.html',
                    modalCtrl: 'cancelarRecursoModalCtrl',
                    name: 'cancelarrecurso',
                    args: {
                        obj: id,
                        windowClass: "app-modal-window-lg"
                    }
                };

                if (!$scope.modalIsLoading) {
                    dtnModal.modalForm(modalConfiguration, $scope);
                }

                $scope.$on('modalOkcancelarrecurso', function (event, retornoModal) {
                    $scope.load();
                });

                $scope.$on('modalCancelcancelarrecurso', function () {
                    $scope.load();
                });
            }

            $scope.abrirPdf = function (byteArquivo, nameModal) {

                if (byteArquivo != null) {

                    var blobFile = dataURItoBlob("data:application/pdf;base64," + byteArquivo);

                    return saveAs(blobFile, nameModal || "Documento Recurso Online " + detranUtil.dtnDateNowConcat());

                } else {
                    showMsg(growl, "Arquivo inválido!");
                }
            };

            $scope.rulesToDisableButton = function (obj) {

                var situacaoRecurso = _.result(_.find(obj.linha, {'field': 'entidade.situacao'}), 'value');

                if (_.isEqual(obj.button.name, "editar")) {

                    if (!_.isEqual(situacaoRecurso, "BACKOFFICE")) {
                        return true;
                    }
                }

                if (_.isEqual(obj.button.name, "gerarPdf")) {

                    if (!_.isEqual(situacaoRecurso, "EFETIVADO")) {
                        return true;
                    }
                }

                if (_.isEqual(obj.button.name, "cancelar")) {

                    if (!_.isEqual(situacaoRecurso, "BACKOFFICE")) {
                        return true;
                    }
                }
            };

            if (_.isEqual($scope.page, "edit") || _.isEqual($scope.page, "view")) {

                $scope.editar(config.urls.editar, '{"id": ' + $rootScope.idPaginaAtual + '}').then(function (retorno) {

                    if (!_.isEmpty(retorno.entity[0]) || !_.isEmpty(retorno.entity[0].entidade))

                        $scope.data = retorno.entity[0];

                    /* Preenche os dados iniciais dos ANEXOS e guarda na lista temporaria */
                    $scope.listaTemporaria = [];

                    $scope.appFiles = {};
                    $scope.appFiles.attachFiles = new dtnAttachFiles();
                    config.attachModalPath = ($rootScope.urlPartials || 'partials/') + 'modais/modalAnexos.html';
                    $scope.appFiles.attachFiles.setConfig($scope.config);

                    $scope.appFiles.hideElements = {
                        labelRestricoes: false,
                        legend: false,
                        botaoAnexar: true,
                        botoesExcluir: true
                    };

                    $scope.fnCallAddParamsFiles = function (item, obj) {

                        item = obj.arquivoPA;
                        item.excluir = false;
                        item.imageViewer = "gallery";

                        if (angular.isDefined(item.id)) {
                            obj.id = angular.copy(item.id);
                        }

                        item.typeApplication = item.extensao ? "data:" + item.extensao.toLowerCase() + ";base64," : null;
                        item.noCheckByte = true;
                        item.labelDoc = item.descricao;

                        return item;
                    };

                    // anexos.
                    documentos = null;

                    /* recuperar arquivos */
                    $scope.appFiles.attachFiles.setAttachedFiles($scope.data.documentos, $scope.fnCallAddParamsFiles);

                    documentos = $scope.appFiles.attachFiles.getListAttachedFiles();

                    $scope.openFile = function (item) {

                        /* if (item.byteArquivo != null && item.extensao === 'PDF') {

                             var blobFile = dataURItoBlob("data:application/pdf;base64," + item.byteArquivo);

                             return saveAs(blobFile, item.descricao + detranUtil.dtnDateNowConcat());

                         } else {*/

                        //item.imageViewer = "single";
                        //item.nameModal = item.descricao;
                        item.imageViewer = "gallery";
                        item.nameModal = item.descricao || 'Sem Descrição';
                        item.pdfViewer = true;

                        var tpAp = $scope.appFiles.attachFiles.getTypeFile(item);

                        if (tpAp != null) {
                            $scope.appFiles.attachFiles.openFile(item, $scope);
                            return;
                        }

                        $scope.appFiles.attachFiles.openFile(item, $scope);
                        // }
                    };
                });

                $scope.efetivar = function () {

                    var confirm = dialog.confirm(" Confirmar Registro do Recurso?", "  ");

                    confirm.result.then(function (btn) {

                        $scope.gravar($scope.urls.gravar, $scope.data, 'btnSaveUpdate').then(function (retorno) {

                            if (retorno.error != undefined && retorno.error != '') {
                                showMsg(growl, retorno.error);
                                $rootScope.loading = false;
                                return;
                            }

                            $scope.clear();
                            $scope.killAction = false;
                            $scope.load();

                            $location.path(config.urlPath);

                            showMsgSuccess(growl, retorno.message);

                        }).then(function () {
                            $scope.killAction = true;
                        });
                    }, function (btn) {
                        console.log('nao confirmado');
                    });
                };

                $scope.recusar = function () {

                    var modalConfiguration = {
                        tplUrl: config.filesPath + '/recusar.html',
                        modalCtrl: 'recusarRecursoModalCtrl',
                        name: 'recusarrecurso',
                        args: {
                            obj: $scope.data,
                            url: config.urls.recusar,
                            windowClass: "app-modal-window-md"
                        }
                    };

                    if (!$scope.modalIsLoading) {
                        dtnModal.modalForm(modalConfiguration, $scope);
                    }

                    $scope.$on('modalCancelrecusarrecurso', function (event, retornoModal) {
                        $scope.load();
                    });
                };
            }
        }
    ])

    .controller('cancelarRecursoModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", "args", "$injector", "$location", "backofficeConfig",
        function ($scope, $modalInstance, growl, srvRest, args, $injector, $location, config) {

            $scope.data = {
                id: args.obj
            };

            $scope.validateForm = function () {
                return detranUtil.ehBrancoOuNulo($scope.data.motivoCancelamento);
            };

            $scope.ok = function () {

                srvRest.gravar('detran-infracao/resource/backoffices/cancelar', $scope.data).then(function (retorno) {

                    if (!_.isEmpty(retorno.error)) {
                        detranUtil.showMsg(growl, retorno.error);
                    } else {
                        showMsgSuccess(growl, retorno.message);
                        $modalInstance.close(retorno);
                    }

                }, function (err) {
                    detranUtil.showMsg(growl, err.error);
                });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        }
    ])

    .controller('recusarRecursoModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", "args", "$injector", "$location", "backofficeConfig",
        function ($scope, $modalInstance, growl, srvRest, args, $injector, $location, config) {

            $injector.invoke(selectCtrl, this, {$scope: $scope});

            $scope.page = 'create';
            $scope.data = {id: args.obj.id};

            $scope.validateForm = function () {
                return detranUtil.ehBrancoOuNulo($scope.data.motivoRecusa);
            };

            $scope.ok = function () {

                srvRest.gravar(args.url, $scope.data, 'btnSaveUpdate').then(function (retorno) {

                    $location.path(config.urlPath);

                    if (!_.isEmpty(retorno.error)) {
                        detranUtil.showMsg(growl, retorno.error);
                    } else {
                        showMsgSuccess(growl, retorno.message);
                        $modalInstance.dismiss();
                    }
                }, function (err) {
                    detranUtil.showMsg(growl, err.error);
                });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };
        }
    ]);