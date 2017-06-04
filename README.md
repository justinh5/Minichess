# Minichess Player

This player uses adversarial search AI for online minichess matches on [IMCS](https://github.com/BartMassey/IMCS).
The strategy for finding the next best move makes a depth-first negamax search with alpha-beta pruning and iterative deepening 
to foresee what moves could potentially lead to win (or a loss).

The transposition table features currently have too many many bugs to work properly.

### Negamax with Alpha-Beta pruning:

```
function negamax(state, depth, α, β)
     
     if time > endTime
        return 0
        
     if state is a final state or depth = 0
        return score(state)
        
     Moves <- GenerateMoves(state)
     Moves <- Shuffle(Moves)
     Moves <- OrderMoves(Moves)
     
     bestValue <- −∞
     
     for move in Moves
         state <- movePiece(move)
         value <- −negamax(state, depth − 1, −β, −α)
         bestValue <- max(bestValue, value)
         α <- max(α, value)
         state <- undoMove(move)
         
         if α ≥ β
           break
           
     return bestValue
```


### IMCS Play and Connectivity

The IMCS server hosts an assortment of commands, but this program interface
only makes a subset of them available for the user with enough capability to
play games and carry out basic tasks. An IMCS account must be created in order to play online.

Visit http://wiki.cs.pdx.edu/minichess/ for instructions on how to connect to IMCS

