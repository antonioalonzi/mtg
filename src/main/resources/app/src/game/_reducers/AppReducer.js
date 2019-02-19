export default (state, action) => {
  const newState = Object.assign({}, state)

    switch (action.type) {
    case 'INIT_WAITING_OPPONENT':
      return Object.assign(newState, {message: 'Waiting for opponent...'})

    case 'OPPONENT_JOINED':
      return Object.assign(newState, {message: undefined})

    case 'INIT_PLAYER':
      return Object.assign(newState, {player: action.value})

    case 'INIT_OPPONENT':
      return Object.assign(newState, {opponent: action.value})

    case 'UPDATE_TURN':
      //if (action.value.currentPhaseActivePlayer === state.player.name) {
      //  stompClient.sendEvent('turn', {action: 'PASS_PRIORITY'})
      //}

      return Object.assign(newState, {turn: action.value})

    case '@@INIT':
      return {}

    default:
      throw 'Unknown action type ' + action.type
  }
}