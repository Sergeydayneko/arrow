package arrow.instances

import arrow.*
import arrow.core.Eval
import arrow.data.*
import arrow.typeclasses.*

@instance(Validated::class)
interface ValidatedFunctorInstance<E> : Functor<ValidatedKindPartial<E>> {
    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.reify().map(f)
}

@instance(Validated::class)
interface ValidatedApplicativeInstance<E> : ValidatedFunctorInstance<E>, Applicative<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>

    override fun <A> pure(a: A): Validated<E, A> = Valid(a)

    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.reify().map(f)

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: Kind<ValidatedKindPartial<E>, (A) -> B>): Validated<E, B> = fa.reify().ap(ff.reify(), SE())

}

@instance(Validated::class)
interface ValidatedApplicativeErrorInstance<E> : ValidatedApplicativeInstance<E>, ApplicativeError<ValidatedKindPartial<E>, E> {

    override fun <A> raiseError(e: E): Validated<E, A> = Invalid(e)

    override fun <A> handleErrorWith(fa: ValidatedKind<E, A>, f: (E) -> ValidatedKind<E, A>): Validated<E, A> =
            fa.reify().handleLeftWith(f)

}

@instance(Validated::class)
interface ValidatedFoldableInstance<E> : Foldable<ValidatedKindPartial<E>> {

    override fun <A, B> foldLeft(fa: ValidatedKind<E, A>, b: B, f: (B, A) -> B): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ValidatedKind<E, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.reify().foldRight(lb, f)

}

@instance(Validated::class)
interface ValidatedTraverseInstance<E> : ValidatedFoldableInstance<E>, Traverse<ValidatedKindPartial<E>> {

    override fun <G, A, B> traverse(fa: Kind<ValidatedKindPartial<E>, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Validated<E, B>> =
            fa.reify().traverse(f, GA)

}

@instance(Validated::class)
interface ValidatedSemigroupKInstance<E> : SemigroupK<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>

    override fun <B> combineK(x: ValidatedKind<E, B>, y: ValidatedKind<E, B>): Validated<E, B> =
            x.reify().combineK(y, SE())

}

@instance(Validated::class)
interface ValidatedEqInstance<L, R> : Eq<Validated<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Validated<L, R>, b: Validated<L, R>): Boolean = when (a) {
        is Valid -> when (b) {
            is Invalid -> false
            is Valid -> EQR().eqv(a.a, b.a)
        }
        is Invalid -> when (b) {
            is Invalid -> EQL().eqv(a.e, b.e)
            is Valid -> false
        }
    }
}
