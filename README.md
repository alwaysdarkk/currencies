# currencies
Create various types of currencies and set up a commerce system between the players on your Hytale server.

The plugin uses [Hibernate](https://hibernate.org/) to handle the connection to [MySQL](https://github.com/brettwooldridge/HikariCP)/[H2](https://www.h2database.com/html/main.html).

## API Methods
The plugin includes "API" methods for integration with other plugins.

- CurrencyUser: Contains all methods for updating the user's balance.
- Currency: Contains all the methods for retrieving information about a created currency.

```java
final PlayerRef playerRef = ...;
final String currencyId = ...;

final CurrenciesPlugin currenciesPlugin = CurrenciesPlugin.getInstance();

final CurrencyUser user = currenciesPlugin.getUserCache().find(playerRef.getUuid());
final Currency currency = currenciesPlugin.getCurrencyCache().find(currencyId);
```

## Example config
The plugin generates a `config.json` file during initialization that contains the details for creating currencies and connecting to the database.

## Currencies field:
- Id: Id of currency to general use in plugin
- Name: Name displayed in plugin messages.
- Color: Color displayed in plugin messages.
- DefaultAmount: Initial balance amount that the player has of this currency.
- IsPayEnable: Determines whether players will be able to use the pay command.

## Repository field:
- Type: Type of connection. (h2 or mysql)
- Address: Address to MySQL connection.
- Username: Username to MySQL connection.
- Password: Password to MySQL connection.
- Database: Database of MySQL connection.
- File: File path and name to H2 connection.

```json
{
  "Currencies": [
    {
      "Id": "coins",
      "Name": "Coins",
      "Color": "#09ff00",
      "DefaultAmount": 100.0,
      "IsPayEnable": true
    },
    {
      "Id": "gold",
      "Name": "Gold",
      "Color": "#FFD700",
      "DefaultAmount": 0.0,
      "IsPayEnable": false
    }
  ],
  "Repository": {
    "Type": "h2",
    "Address": "localhost:3306",
    "Username": "root",
    "Password": "",
    "Database": "test",
    "File": "database/data"
  }
}
```

## Commands and permissions
- /{currency-id} [player] - Shows your or a player balance.
- /{currency-id} pay {player} {amount} - Send balance for a player.
- /{currency-id} add {player} {amount} - Add balance for a player. (currencies.commands.add)
- /{currency-id} remove {player} {amount} - Remove balance of a player. (currencies.commands.remove)
- /{currency-id} set {player} {amount} - Set balance for a player. (currencies.commands.set)
