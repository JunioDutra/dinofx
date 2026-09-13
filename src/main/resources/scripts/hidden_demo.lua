return {
  setup = function()
    engine.state.set('loaded', true)
  end,
  update = function(delta)
    engine.state.set('frames', (engine.state.get('frames') or 0) + 1)
    engine.state.set('last_delta', delta)
  end,
  dispose = function()
    engine.state.set('disposed', true)
  end
}
