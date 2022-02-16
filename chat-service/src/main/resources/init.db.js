db.createCollection( "log", { capped: true, size: 100000 } )
db.createCollection( "event", { capped: true, size: 100000 } )
db.getCollection('user').createIndex(
    {
        pseudo: 1
    },
    {
        background: true,
        name: "user_pseudo",
        unique: true
    }
)
