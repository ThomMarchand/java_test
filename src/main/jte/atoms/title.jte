@import com.app.ui.TitleVariant

@param String label
@param TitleVariant variant

@if(variant == TitleVariant.H1)
  <h1>${label}</h1>
@elseif(variant == TitleVariant.H2)
  <h2>${label}</h2>
@elseif(variant == TitleVariant.H3)
  <h3>${label}</h3>
@endif
