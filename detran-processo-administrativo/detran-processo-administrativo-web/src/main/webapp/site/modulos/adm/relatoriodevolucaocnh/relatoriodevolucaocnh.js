angular.module('relatoriodevolucaocnh', ['ngRoute'])

.constant("relatoriodevolucaocnhConfig", {
    name: 'relatoriodevolucaocnh',
    urls: {
        "emitir": 'detran-processo-administrativo/resource/relatoriodevolucaocnhs/emitir'
    },
    model: [
        {field: "entidade.id", hidden: "hide"},
        {field: "entidade.processoAdministrativo.numeroProcessoMascarado", displayName: "relatoriodevolucaocnh.label.numeroProcesso"},
        {field: "entidade.processoAdministrativo.cpf", displayName: "relatoriodevolucaocnh.label.cpf"},
        {field: "entidade.logradouro", displayName: "relatoriodevolucaocnh.label.logradouro"},
        {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
            {name: "reativar", icon: "global.btn.reativar.icon", btn:"global.btn.reativar.btn", title:"global.btn.reativar.tooltip"},
            {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title:"global.btn.desativar.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/relatoriodevolucaocnh',
    urlPath: '/adm/relatoriodevolucaocnh',
    i18nextResources: {
        "relatoriodevolucaocnh":{
            "titulo":"Relatório Devolução CNH(BETA)", 
            "label": {
                
            }
        }
    }
})

.config(["$routeProvider", "relatoriodevolucaocnhConfig", function ($rp, config) {
  var path = config.filesPath;
  var urlPath = config.urlPath;
  var name = config.name;

  $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'relatoriodevolucaocnh.titulo'})
}])

.controller('relatoriodevolucaocnhCtrl', ["$scope", "$injector", "relatoriodevolucaocnhConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('relatoriodevolucaocnhList', ['$scope', '$controller', '$injector',
    function($scope, $controller, $injector){
        $scope.page = 'list';
        $controller('relatoriodevolucaocnhCustomCtrl', {$scope: $scope});
        
    }
])

.controller('relatoriodevolucaocnhCustomCtrl', ["$scope", "$controller", "relatoriodevolucaocnhConfig", "$rootScope", "$injector", 
                                              "detranModal", "growl", "dtnAttachFiles", "$rootScope", "utils",
    function($scope, ctrl, config, $rootScope, $injector, dtnModal, growl, dtnAttachFiles, $rootScope, utils) {
        ctrl('relatoriodevolucaocnhCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
         
         
        $scope.appFiles = {};
        $scope.appFiles = new dtnAttachFiles();
        $scope.appFiles.setConfig($scope.config);
         
        $scope.emitir = function(){
            $scope.editar(config.urls.emitir, {}).then(function (retorno) {

                if (!_.isEmpty(retorno.error)) {

                    detranUtil.showMsg(growl,retorno.error);

                } else {

                    var byteArquivo = retorno.objectResponse;

                    if (detranUtil.ehBrancoOuNulo(byteArquivo)) {

                        detranUtil.showMsg(growl, "Arquivo inválido.");
                        $rootScope.loading = false;
                        return
                    }

                    var item = { 
                        byteArquivo : byteArquivo,
                        extensao : "PDF",
                        usePdfJS : false,
                        nameModal : "Relatório Devolução CNH",
                        orientation : 'landscape'
                    };

                    dtnAttachFiles().openFile(item, $scope);
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        }
        
    }
]);