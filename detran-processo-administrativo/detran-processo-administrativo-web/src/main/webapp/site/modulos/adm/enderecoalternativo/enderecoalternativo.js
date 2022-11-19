angular.module('enderecoalternativo', ['ngRoute'])

.constant("enderecoalternativoConfig", {
    name: 'enderecoalternativo',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/enderecoalternativos/search',
        "gravar":    'detran-processo-administrativo/resource/enderecoalternativos/save',
        "desativar": 'detran-processo-administrativo/resource/enderecoalternativos/',
        "reativar":  'detran-processo-administrativo/resource/enderecoalternativos/reativar',
        "buscarProcessoAdministrativo":  'detran-processo-administrativo/resource/processoadministrativos/search',
        "novo":      'detran-processo-administrativo/resource/enderecoalternativos/new'
    },
    model: [
        {field: "entidade.id", hidden: "hide"},
        {field: "entidade.processoAdministrativo.numeroProcessoMascarado", displayName: "enderecoalternativo.label.numeroProcesso"},
        {field: "entidade.processoAdministrativo.cpf", displayName: "enderecoalternativo.label.cpf"},
        {field: "entidade.logradouro", displayName: "enderecoalternativo.label.logradouro"},
        {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
            {name: "reativar", icon: "global.btn.reativar.icon", btn:"global.btn.reativar.btn", title:"global.btn.reativar.tooltip"},
            {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title:"global.btn.desativar.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/enderecoalternativo',
    urlPath: '/adm/enderecoalternativo',
    i18nextResources: {
        "enderecoalternativo":{
            "titulo":"Endereço Alternativo", 
            "label": {
                "numeroProcesso":"Nº Processo", 
                "cpf":"CPF",
                "nome":"Nome",
                "tipoEnvolvido":"Envolvido",
                "logradouro":"Logradouro",
                "numero":"Número",
                "complemento":"Complemento",
                "cep":"CEP",
                "estado":"UF",
                "municipio":"Município",
                "bairro":"Bairro"
            }
        }
    }
})

.config(["$routeProvider", "enderecoalternativoConfig", function ($rp, config) {
  var path = config.filesPath;
  var urlPath = config.urlPath;
  var name = config.name;

  $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'enderecoalternativo.titulo'})
    .when(urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when(urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'});
}])

.controller('enderecoalternativoCtrl', ["$scope", "$injector", "enderecoalternativoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('enderecoalternativoList', ['$scope', '$controller', '$injector',
    function($scope, $controller, $injector){
        $scope.page = 'list';
        $controller('enderecoalternativoCustomCtrl', {$scope: $scope});

    }
])

.controller('enderecoalternativoCreate', ['$scope', '$parse', '$controller',
    function($scope, $parse, $controller){
        $scope.page = 'create';
        $controller('enderecoalternativoCustomCtrl', {$scope: $scope});
    }
])

.controller('enderecoalternativoView', ["$scope", "$controller", '$rootScope',
    function($scope, $controller, $rootScope){
        $scope.page = 'view';
        $controller('enderecoalternativoCustomCtrl', {$scope: $scope});
    }
])

.controller('enderecoalternativoCustomCtrl', ["$scope", "$controller", "enderecoalternativoConfig", "$rootScope", "$injector", 
                                              "detranModal", "growl", "dtnAttachFiles", "$rootScope", "utils",
    function($scope, ctrl, config, $rootScope, $injector, dtnModal, growl, dtnAttachFiles, $rootScope, utils) {
        ctrl('enderecoalternativoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.data={entidade:{}};
        
        $scope.showList = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.new = function () {
            console.log("New: ", $scope.data);
            $scope.data={};
            $scope.data.entidade={};
            $scope.municipioSelect = [];
            $scope.clear();
        };
        
        if($scope.page == 'view'){
            $scope.editar($scope.urls.pesquisar, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function(retorno){
                $scope.data = retorno.entity[0];
            });
        }
        
        $scope.buscarProcessoAdministrativo =  function(numeroPa){
            if ($scope.page != 'view') {
                if(detranUtil.ehBrancoOuNulo(numeroPa))
                    return;
                $scope.carregar(config.urls.buscarProcessoAdministrativo, {numeroProcesso: numeroPa}).then(function (retorno) {
                    $scope.data.entidade.processoAdministrativo = null; 
                    if (retorno.error != "") {
                        detranUtil.showMsg(growl,retorno.error);
                    }else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                        detranUtil.showMsgWarn(growl,retorno.warning);
                    }else{
                        $scope.data.entidade.processoAdministrativo = retorno.entity[0].entidade;
                    }    
                });
            }
        };
        
        $scope.carregarMunipicioPorUf = function(obj){
            var estadoSelecionado = utils.convertSelectItemToObject(obj);
            if (estadoSelecionado && estadoSelecionado != null) {
                $scope.carregar("municipios/searchMunicipioPorUf", estadoSelecionado.sigla).then(function (retorno) {
                    $scope.municipioSelect = [];
                    retorno.listSelectItem = retorno.listSelectItem || {};
                    var municipios = retorno.listSelectItem[0] || {};
                    if (angular.isDefined(municipios.listValue)) {
                        for (var a = 0; a < municipios.listValue.length; a++) {
                            $scope.municipioSelect.push({value: municipios.listValue[a].value, label: municipios.listValue[a].label, object: municipios.listValue[a].object.selected});
                        }
                    }
                });
            } else{
                $scope.municipio = null;
                $scope.data.entidade.municipio = null;
            }
        };
        
        $scope.selecionarMunicipio = function(municipio){
            var municipioSelecionado = utils.convertSelectItemToObject(municipio);
            if(municipioSelecionado){
                $scope.data.entidade.municipio = municipioSelecionado.id;
            }
        };
        
        $scope.validateForm = function() {
            if ($scope.data.entidade === undefined)return true;
            if (detranUtil.ehBrancoOuNulo($scope.data.entidade.processoAdministrativo)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.processoAdministrativo.numeroProcessoMascarado)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.tipoEnvolvido)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.logradouro)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.numero)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.bairro)
                || detranUtil.ehBrancoOuNulo($scope.data.entidade.cep)) 
                return true;
            return false;
        };
        
        $scope.preSave = function() {
            $scope.save();
        };
    }
]);