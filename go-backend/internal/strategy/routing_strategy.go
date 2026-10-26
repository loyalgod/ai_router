package strategy

import "github.com/loyalgod/ai_router/go-backend/internal/model/entity"

type RoutingStrategy interface {
	SelectModel(models []entity.Model, requestedModel string) *entity.Model
	GetFallbackModels(models []entity.Model, requestedModel string) []entity.Model
	GetStrategyType() string
}
