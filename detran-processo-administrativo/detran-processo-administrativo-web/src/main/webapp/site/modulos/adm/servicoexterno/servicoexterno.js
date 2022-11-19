angular.module('servicoexterno', ['ngRoute'])

.constant("servicoexternoConfig", {
    name: 'servicoexterno',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/servicoexternos/search',
        "gravar":    'detran-processo-administrativo/resource/servicoexternos/save',
        "novo":      'detran-processo-administrativo/resource/servicoexternos/new',
        "desativar": 'detran-processo-administrativo/resource/servicoexternos/',
        "reativar":  'detran-processo-administrativo/resource/servicoexternos/reativar',
        
    },
    model: [
        {field: "entidade.id", hidden: "hide"},
        {field: "entidade.versaoRegistro", hidden: "hide"},
        {field: "entidade.nome", displayName: "servicoexterno.label.nome"}, 
        {field: "entidade.descricao", displayName: "servicoexterno.label.descricao"}, 
        {field: "entidade.data", displayName: "servicoexterno.label.data"}, 
        {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel", hidden: "hide"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
            {name: "reativar", icon: "global.btn.reativar.icon", btn:"global.btn.reativar.btn", title:"global.btn.reativar.tooltip"},
            {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title:"global.btn.desativar.tooltip"}
        ]}
    ],
    filesPath :  '/detran-processo-administrativo/site/modulos/adm/servicoexterno',
    urlPath : '/adm/servicoexterno',
    i18nextResources: {
        "servicoexterno": {
            "titulo": "PA - Serviço Externo",
            "label": {
                "nome":"Serviço",
                "descricao":"Descrição",
                "data":"Data"
            }
        }
    }
})

.config(["$routeProvider", "servicoexternoConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'servicoexterno.titulo'})
    .when( urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when( urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'})
    .when( urlPath + '/editar', {templateUrl: path + '/form.html', controller: name + 'Edit', label: 'global.label.breadcrumb.editar'});
}])

.controller('servicoexternoCtrl', ["$scope", "$injector", "servicoexternoConfig",
    function ($scope, $injector, config) {
        
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('servicoexternoList', ['$scope', '$controller', '$rootScope', 'growl', 'servicoexternoConfig', function($scope, $controller, $rootScope, growl, config){
        $scope.page = 'list';
        $controller('servicoexternoCustomCtrl', {$scope: $scope});
        
        $scope.load();
    }
])

.controller('servicoexternoCreate', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'create';
        $controller('servicoexternoCustomCtrl', {$scope: $scope});
    }
])

.controller('servicoexternoView', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'view';
        $controller('servicoexternoCustomCtrl', {$scope: $scope});
    }
])

.controller('servicoexternoEdit', ["$scope", "$controller", '$rootScope',
    function($scope, $controller, $rootScope){
        $scope.page = 'edit';
        $controller('servicoexternoCustomCtrl', {$scope: $scope});
    }
])

.controller('servicoexternoCustomCtrl', ["$scope", "$controller", "servicoexternoConfig", "$rootScope", "$injector", "utils", "$filter", "growl", "detranModal", "dtnAttachFiles", "$location", "validatePermission",
    function($scope, ctrl, config, $rootScope, $injector, utils, $filter, growl, dtnModal, dtnAttachFiles, $location, validatePermission){
        ctrl('servicoexternoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.data = {entidade:{}};
        
        if ($scope.page === 'edit' || $scope.page === 'view') {
            
            $scope.editar($scope.urls.pesquisar, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function(retorno){
                $scope.data = retorno.entity[0];
            });
        }
        
        if ($scope.page === 'edit' || $scope.page === 'create') {
            
            
            $scope.validateForm = function() {
                
                if (!$scope.dataChanged()) 
                    return true;
                
                if (!angular.isUndefined($scope.data)) {
                    
                    if (!angular.isUndefined($scope.data.entidade)) {
                        
                        if (!detranUtil.ehBrancoOuNulo($scope.data.entidade.nome)
                                && !detranUtil.ehBrancoOuNulo($scope.data.entidade.descricao)
                                ) {

                            return false;
                        }
                    }
                }
                
                return true;
            };
            
            $scope.preSave = function() {
                
                $scope.save();
                
            };
        }
}])
;
