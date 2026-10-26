"""SDK exceptions."""

from __future__ import annotations


class AiRouterException(Exception):
    """Base exception for SDK errors."""

    def __init__(self, message: str, status_code: int | None = None) -> None:
        super().__init__(message)
        self.status_code = status_code


class AuthException(AiRouterException):
    """Raised when auth fails."""


class RateLimitException(AiRouterException):
    """Raised when request is rate limited."""
