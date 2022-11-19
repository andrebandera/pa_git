detranUtil.modules.items["processoadministrativo"] = {
    path : "/detran-processo-administrativo/site/modulos/adm",
    features : [
        {
            module: 'processoadministrativo', 
            source: ['processoadministrativo/processoadministrativo.js'], 
            urlPath: '/adm/processoadministrativo',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo',
            dependencies:{
                modules:["processoadministrativoinfracao", 
                         "processojudicialpa", 
                         "processoadministrativorecurso", 
                         "processoadministrativobloqueio", 
                         "processoadministrativocontrolecnh", 
                         "processoadministrativonotificacao", 
                         "processoadministrativomovimentacoes",
                         "processoadministrativohistorico"],
                source: [
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativoinfracao/processoadministrativoinfracao.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processojudicialpa/processojudicialpa.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativorecurso/processoadministrativorecurso.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativobloqueio/processoadministrativobloqueio.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativocontrolecnh/processoadministrativocontrolecnh.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativonotificacao/processoadministrativonotificacao.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativomovimentacoes/processoadministrativomovimentacoes.js',
                    '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativohistorico/processoadministrativohistorico.js',
                ]
            }
        },
        {
            module: 'lotenotificacao',
            source: ['lotenotificacao/lotenotificacao.js'],
            urlPath: '/adm/lotenotificacao',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/lotenotificacao',
            dependencies:{
                modules:["notificacaolote"],
                source: [
                    '/detran-processo-administrativo/site/modulos/adm/lotenotificacao/notificacaolote/notificacaolote.js',
                ]
            }
        },
        {
            module: 'consultamovimentacaopa', 
            source: ['consultamovimentacaopa/consultamovimentacaopa.js'], 
            urlPath: '/adm/consultamovimentacaopa',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/consultamovimentacaopa'
        },
        {
            module: 'desistenterecursoinstauracao', 
            source: ['desistenterecursoinstauracao/desistenterecursoinstauracao.js'], 
            urlPath: '/adm/desistenterecursoinstauracao',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/desistenterecursoinstauracao'
        },
        {
            module: 'controlecnhpa',
            source: ['controlecnhpa/controlecnhpa.js'],
            urlPath: '/adm/controlecnhpa',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/controlecnhpa',
            dependencies: {
                source: ['/detran-processo-administrativo/site/modulos/adm/responsavelprot/responsavelprot.js']
            }
        },
        {
            module: 'recurso',
            source: ['recurso/recurso.js'],
            urlPath: '/adm/recurso',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/recurso',
            dependencies: {
               source: ['/detran-processo-administrativo/site/modulos/adm/responsavelprot/responsavelprot.js']
            }
        },
        {
            module: 'servicoexterno',
            source: ['servicoexterno/servicoexterno.js'],
            urlPath: '/adm/servicoexterno',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/servicoexterno'
        },
        {
            module: 'enderecoalternativo',
            source: ['enderecoalternativo/enderecoalternativo.js'],
            urlPath: '/adm/enderecoalternativo',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/enderecoalternativo'
        },
        {
            module: 'execucaoandamento',
            source: ['execucaoandamento/execucaoandamento.js'],
            urlPath: '/adm/execucaoandamento',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/execucaoandamento'
        },
        {
            module: 'cadastroretornoar',
            source: ['cadastroretornoar/cadastroretornoar.js'],
            urlPath: '/adm/cadastroretornoar',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/cadastroretornoar'
        },
        {
            module: 'backoffice',
            source: ['backoffice/backoffice.js'],
            urlPath: '/adm/backoffice',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/backoffice'
        },
        {
            module: 'relatoriodevolucaocnh',
            source: ['relatoriodevolucaocnh/relatoriodevolucaocnh.js'],
            urlPath: '/adm/relatoriodevolucaocnh',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/relatoriodevolucaocnh'
        },
        {
            module: 'movimentacao',
            source: ['movimentacao/movimentacao.js'],
            urlPath: '/adm/movimentacao',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/movimentacao'
        },
        //APO
         {
            module: 'statusandamento',
            source: ['apo/statusandamento/statusandamento.js'],
            urlPath: '/adm/apo/statusandamento',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/statusandamento',
            dependencies: {
                modules: ['andamento'],
                source: ["/detran-processo-administrativo/site/modulos/adm/apo/statusandamento/andamento/andamento.js"]
            }
        },
        {
            module: 'andamento',
            source: ['andamento.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/statusandamento/andamento',
            urlPathMestre: '/adm/apo/statusandamento',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/statusandamento'
        },
        {
            module: 'andamentopa',
            source: ['apo/andamentopa/andamentopa.js'],
            urlPath: '/adm/apo/andamentopa',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa',
            dependencies: {
                modules: ['status','andamentofluxofase'],
                source: ["/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/status/status.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/andamentofluxofase/andamentofluxofase.js"]
            }
        },
        {
            module: 'status',
            source: ['status.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/status',
            urlPathMestre: '/adm/apo/andamentopa',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa'
        },
        {
            module: 'andamentofluxofase',
            source: ['andamentofluxofase.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/andamentofluxofase',
            urlPathMestre: '/adm/apo/andamentopa',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa'
        },
        {
            module: 'faseprocessopa',
            source: ['apo/faseprocessopa/faseprocessopa.js'],
            urlPath: '/adm/apo/faseprocessopa',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa',
            dependencies: {
                modules: ['fasefluxoprocesso'],
                source: ["/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa/fasefluxoprocesso/fasefluxoprocesso.js" ]
            }
        },
        {
            module: 'fasefluxoprocesso',
            source: ['fasefluxoprocesso.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa/fasefluxoprocesso',
            urlPathMestre: '/adm/apo/faseprocessopa',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa'
        },
        {
            module: 'fluxoprocesso',
            source: ['apo/fluxoprocesso/fluxoprocesso.js'],
            urlPath: '/adm/apo/fluxoprocesso',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso',
            dependencies: {
                modules: ['origemInstauracao','fluxoprocessofase','fluxofase','tipocorpoandamento','fluxofaseretorno','fluxoandamento','destinofase'],
                source: ["/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/origemInstauracao/origemInstauracao.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxoprocessofase.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxofase.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/tipocorpoandamento/tipocorpoandamento.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxofaseretorno/fluxofaseretorno.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxoandamento/fluxoandamento.js",
                         "/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/destinofase/destinofase.js"]
            }
        },
        {
            module: 'origemInstauracao',
            source: ['origemInstauracao.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/origemInstauracao',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'fluxoprocessofase',
            source: ['fluxoprocessofase.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'fluxofase',
            source: ['fluxofase.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'tipocorpoandamento',
            source: ['tipocorpoandamento.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/tipocorpoandamento',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'fluxofaseretorno',
            source: ['fluxofaseretorno.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxofaseretorno',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'fluxoandamento',
            source: ['fluxoandamento.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxoandamento',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'destinofase',
            source: ['destinofase.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/destinofase',
            urlPathMestre: '/adm/apo/fluxoprocesso',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso'
        },
        {
            module: 'apoioorigeminstauracao',
            source: ['apo/apoioorigeminstauracao/apoioorigeminstauracao.js'],
            urlPath: '/adm/apo/apoioorigeminstauracao',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/apoioorigeminstauracao',
            dependencies: {
                modules: ['fluxo'],
                source: ["/detran-processo-administrativo/site/modulos/adm/apo/apoioorigeminstauracao/fluxoprocessoorigem/fluxo.js"]
            }
        },
        {
            module: 'fluxo',
            source: ['fluxo.js'],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/apoioorigeminstauracao/fluxoprocessoorigem',
            urlPathMestre: '/adm/apo/apoioorigeminstauracao',
            filesPathMestre: '/detran-processo-administrativo/site/modulos/adm/apo/apoioorigeminstauracao'
        },
        {
            module: 'processojudicial', 
            source: ['processojudicial/processojudicial.js'], 
            urlPath: '/adm/processojudicial',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/processojudicial'
        },
        {
            module: 'cnhcartorio', 
            source: ['cnhcartorio/cnhcartorio.js'], 
            urlPath: '/adm/cnhcartorio',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/cnhcartorio'
        },
        {
            module: 'relatorioprocessoprescrito', 
            source: ['relatorioprocessoprescrito/relatorioprocessoprescrito.js'], 
            urlPath: '/adm/relatorioprocessoprescrito',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/relatorioprocessoprescrito'
        },
        {
            module: 'relatorioprazonotificacoes', 
            source: ['relatorioprazonotificacoes/relatorioprazonotificacoes.js'], 
            urlPath: '/adm/relatorioprazonotificacoes',
            filesPath: '/detran-processo-administrativo/site/modulos/adm/relatorioprazonotificacoes'
        }
    ]
};     