package arrow.free.instances

import arrow.*
import arrow.core.FunctionK
import arrow.free.*
import arrow.typeclasses.*

@instance(FreeApplicative::class)
interface FreeApplicativeFunctorInstance<S> : Functor<FreeApplicativeKindPartial<S>> {
    override fun <A, B> map(fa: FreeApplicativeKind<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.reify().map(f)
}

@instance(FreeApplicative::class)
interface FreeApplicativeApplicativeInstance<S> : FreeApplicativeFunctorInstance<S>, Applicative<FreeApplicativeKindPartial<S>> {
    override fun <A> pure(a: A): FreeApplicative<S, A> = FreeApplicative.pure(a)

    override fun <A, B> ap(fa: FreeApplicativeKind<S, A>, ff: FreeApplicativeKind<S, (A) -> B>): FreeApplicative<S, B> =
            fa.reify().ap(ff.reify())

    override fun <A, B> map(fa: FreeApplicativeKind<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.reify().map(f)
}

data class FreeApplicativeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<Kind<FreeApplicativeKindPartial<F>, A>> {
    override fun eqv(a: Kind<FreeApplicativeKindPartial<F>, A>, b: Kind<FreeApplicativeKindPartial<F>, A>): Boolean =
            a.reify().foldMap(interpreter, MG) == b.reify().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeApplicativeEq<F, G, A> =
                FreeApplicativeEq(interpreter, MG)
    }
}
