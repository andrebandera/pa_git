/* 
 * @author: Carlos Eduardo
 * @sinse:
 */

//Create module
angular.module('faseprocessopa', ['ngRoute'])

        .constant("faseprocessopaConfig", {
            name: 'faseprocessopa',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pafaseprocessoadms/search',
                "gravar": 'detran-processo-administrativo/resource/pafaseprocessoadms/save',
                "desativar": 'detran-processo-administrativo/resource/pafaseprocessoadms/',
                "reativar": 'detran-processo-administrativo/resource/pafaseprocessoadms/reativar',
                "novo": 'detran-processo-administrativo/resource/pafaseprocessoadms/new',
                "editar": 'detran-processo-administrativo/resource/pafaseprocessoadms/editar'

            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.codigo", displayName: "faseprocessopa.label.codigo"},
                {field: "entidade.descricao", displayName: "faseprocessopa.label.descricao"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa',
            urlPath: '/adm/apo/faseprocessopa',
            i18nextResources: {
                "faseprocessopa": {
                    "titulo": "Fase Processo",
                    "label": {
                        "codigo": "Código",
                        "descricao": "Descrição"
                    }
                }
            }
        })

//This configures the routes and associates each route with a view and a controller
        .config(["$routeProvider", "faseprocessopaConfig", function ($rp, config) {
                var path = config.filesPath;
                var urlPath = config.urlPath;
                var name = config.name;

                $rp
                        .when(urlPath,
                                {
                                    templateUrl: path + '/index.html',
                                    controller: name + 'List',
                                    label: 'faseprocessopa.titulo'
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
        .controller('faseprocessopaCtrl', ["$scope", "$injector", "faseprocessopaConfig",
            function ($scope, $injector, config) {
                $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            }
        ])

//Listar conteúdos
        .controller('faseprocessopaList', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'list';
                $controller('faseprocessopaCustomCtrl', {$scope: $scope});

                //Carregar dados
                $scope.load();
            }
        ])

        .controller('faseprocessopaCreate', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'create';
                $controller('faseprocessopaCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('faseprocessopaView', ['$scope', '$controller', function ($scope, $controller) {
                $scope.page = 'view';
                $controller('faseprocessopaCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('faseprocessopaEdit', ["$scope", "$controller", '$rootScope',
            function ($scope, $controller, $rootScope) {
                $scope.page = 'edit';
                $controller('faseprocessopaCustomCtrl', {$scope: $scope});
            }
        ])

        .controller('faseprocessopaCustomCtrl', ["$scope", "$controller", "faseprocessopaConfig", "$rootScope", "$injector", "$location","srvMestreDtl",
            function ($scope, ctrl, config, $rootScope, $injector, $location, srvMestreDtl) {
                ctrl('faseprocessopaCtrl', {$scope: $scope});
                $injector.invoke(selectCtrl, this, {$scope: $scope});


                if ($scope.page === 'view' || $scope.page === 'edit') {
                    //Carregar dados
                    $scope.showTabs = true;
                    if (!angular.isUndefined($rootScope.idPaginaAtual)) {
                        $scope.editar($scope.urls.pesquisar, '{"id":"' + $rootScope.idPaginaAtual + '"}').then(function (retorno) {
                            $scope.data = retorno.entity[0];
                            $rootScope.mestre.faseProcessoAdm = srvMestreDtl.setMestre('faseProcessoAdm', $scope.data.entidade);
                        });
                    }
                }

                if ($scope.page === 'edit' || $scope.page === 'create') {
                    ///// Faço a validaçao do meu formulario
                    $scope.validateForm = function () {
                        if (!$scope.dataChanged())
                            return true;

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isDefined($scope.data)) {
                            if ($scope.data.entidade == undefined) return true;
                            if ($scope.data.entidade.codigo && $scope.data.entidade.descricao) 
                                return false;
                        }
                        // bloqueia o botao gravar;
                        return true;
                    };

                    $scope.preSave = function () {

                        $scope.save();
                    };
                }
            }]);